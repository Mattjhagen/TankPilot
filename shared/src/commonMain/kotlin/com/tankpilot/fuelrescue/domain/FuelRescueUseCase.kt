package com.tankpilot.fuelrescue.domain

import com.tankpilot.core.Gallons
import com.tankpilot.core.GeoCoordinate
import com.tankpilot.core.MilesPerGallon
import com.tankpilot.core.StationId
import com.tankpilot.fuel.domain.FuelStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Orchestrates the Fuel Rescue flow (fetch nearby stations, resolve route distances,
 * rank via FuelRescueEngine) as a single shared use case, so the phone app and Android
 * Auto call the same code rather than each re-implementing the plumbing around
 * FuelRescueEngine.evaluateStations. The ranking math itself is untouched — this class
 * only wires existing production pieces together.
 */
class FuelRescueUseCase(
    private val fuelStationRepository: FuelStationRepository,
    private val fuelStateUseCase: FuelStateUseCase,
    private val scenarioOverrideProvider: FuelRescueScenarioOverrideProvider,
    private val scope: CoroutineScope
) {
    private val _recommendations = MutableStateFlow<List<FuelStationRecommendation>>(emptyList())
    val recommendations: StateFlow<List<FuelStationRecommendation>> = _recommendations.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _hasLoadedOnce = MutableStateFlow(false)
    val hasLoadedOnce: StateFlow<Boolean> = _hasLoadedOnce.asStateFlow()

    private val _lastRefreshFailed = MutableStateFlow(false)

    val categories: StateFlow<Map<StationId, Set<RecommendationCategory>>> = _recommendations
        .map { FuelRescueEligibility.categorize(it) }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /**
     * Count of safely-reachable stations for the root screen's glanceable "N safe
     * stations nearby" summary — reuses FuelRescueEligibility, the same rule the full
     * Fuel Rescue flow uses to decide what counts as a real recommendation. Null
     * (never 0) both before a refresh has completed *and* when the last refresh
     * failed (e.g. offline) — a fetch failure must never be shown as "confirmed zero
     * stations found."
     */
    val reachableSafeStationCount: StateFlow<Int?> = combine(
        _recommendations,
        _hasLoadedOnce,
        _lastRefreshFailed
    ) { recs, loaded, failed ->
        if (!loaded || failed) null else recs.count { FuelRescueEligibility.isEligibleForRecommendation(it) }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun refresh(latitude: Double, longitude: Double, forceRefresh: Boolean = false) {
        val vehicle = fuelStateUseCase.currentVehicle.value ?: return

        if (!GeoCoordinate.isValid(latitude, longitude)) {
            _recommendations.value = emptyList()
            _hasLoadedOnce.value = true
            return
        }

        _isRefreshing.value = true
        try {
            val stations = fuelStationRepository.refreshStations(
                latitude = latitude,
                longitude = longitude,
                radiusMiles = 15.0,
                fuelType = vehicle.preferredFuelType,
                forceRefresh = forceRefresh
            )
            val routeDistances = stations.associate { station ->
                // Interim straight-line approximation. RouteDistanceProvider has no real
                // implementation yet (see phases/phase-03a-android-auto-foundation.md,
                // §9.2) — this mirrors the phone app's existing approximation rather
                // than inventing a new one, and is not part of the ranking math itself.
                val routeDist = station.distanceMiles * 1.25 + 0.3
                val duration = routeDist / 35.0 * 60.0
                station.id to (routeDist to duration)
            }

            // Debug-only DHU scenario testing hook (phases/phase-03a-android-auto
            // -foundation.md, Phase 3A.3). Never active in release —
            // NoOpFuelRescueScenarioOverrideProvider always returns null there. Only
            // the *inputs* below can be overridden; FuelRescueEngine.evaluateStations
            // itself always runs unmodified.
            val override = scenarioOverrideProvider.overrideOrNull()
            val estimatedRemaining = override?.estimatedRemaining ?: fuelStateUseCase.estimatedFuelRemaining.value
            val confidenceLevel = override?.confidenceLevel ?: fuelStateUseCase.confidence.value

            _recommendations.value = FuelRescueEngine.evaluateStations(
                estimatedRemaining = estimatedRemaining,
                learnedMpg = MilesPerGallon(vehicle.learnedMpg),
                confidenceLevel = confidenceLevel,
                vehicleFuelType = vehicle.preferredFuelType,
                vehicleFuelGradeKey = vehicle.preferredFuelGrade ?: "regular",
                reserveFuel = Gallons(vehicle.reserveFuelGallons),
                tankCapacity = Gallons(vehicle.tankCapacity),
                candidates = stations,
                routeDistances = routeDistances,
                fallbackPrice = null
            )
            _lastRefreshFailed.value = false
        } catch (e: Exception) {
            // Preserve last-known recommendations on a transient fetch failure rather
            // than clearing them, matching SqlDelightFuelStationRepository's own
            // stale-cache-on-error behavior. reachableSafeStationCount still reports
            // unavailable (not 0) via _lastRefreshFailed below.
            _lastRefreshFailed.value = true
        } finally {
            _isRefreshing.value = false
            _hasLoadedOnce.value = true
        }
    }
}
