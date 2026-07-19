package com.tankpilot.fuel.domain

import com.tankpilot.fuel.FuelEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class ActiveFuelBurnUseCase(
    private val accumulatedDistanceMeters: StateFlow<Double>,
    private val idleTimeSeconds: StateFlow<Long>,
    private val mpgFlow: StateFlow<MpgEstimate>,
    private val displacementLiters: Double?,
    private val cylinderCount: Long?,
    private val scope: CoroutineScope
) {
    private val baseIdleRateGph = FuelEngine.estimateIdleFuelRate(displacementLiters, cylinderCount).value
    private val _idleRateMultiplier = MutableStateFlow(1.0)
    
    var idleRateMultiplier: Double
        get() = _idleRateMultiplier.value
        set(value) {
            _idleRateMultiplier.value = value
        }

    val activeFuelBurn: StateFlow<Double> = combine(
        accumulatedDistanceMeters,
        idleTimeSeconds,
        mpgFlow,
        _idleRateMultiplier
    ) { distanceMeters, idleSeconds, mpgEstimate, multiplier ->
        val mpgVal = mpgEstimate.value ?: 25.0
        val distanceMiles = distanceMeters * 0.000621371
        val drivingBurn = if (mpgVal > 0.0) distanceMiles / mpgVal else 0.0
        
        val idleHours = idleSeconds / 3600.0
        val idleBurn = baseIdleRateGph * multiplier * idleHours
        
        drivingBurn + idleBurn
    }.stateIn(scope, SharingStarted.Eagerly, 0.0)
}
