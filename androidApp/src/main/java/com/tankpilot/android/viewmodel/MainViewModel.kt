package com.tankpilot.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tankpilot.core.*
import com.tankpilot.vehicle.domain.Vehicle
import com.tankpilot.vehicle.domain.VehicleRepository
import com.tankpilot.trip.domain.Trip
import com.tankpilot.trip.domain.TripRepository
import com.tankpilot.trip.domain.DrivingType
import com.tankpilot.fillup.domain.FillUp
import com.tankpilot.fillup.domain.FillUpRepository
import com.tankpilot.fuel.FuelEngine
import com.tankpilot.fuel.domain.FuelStateUseCase
import com.tankpilot.confidence.ConfidenceEngine
import com.tankpilot.fuelrescue.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID

import com.tankpilot.fuel.domain.CalibrationEngine

class MainViewModel(
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository,
    private val fillUpRepository: FillUpRepository,
    private val fuelStationRepository: FuelStationRepository,
    private val fuelStateUseCase: FuelStateUseCase,
    private val calibrationEngine: CalibrationEngine
) : ViewModel() {

    val vehicles = vehicleRepository.getVehicles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentVehicle = MutableStateFlow<Vehicle?>(null)
    val currentVehicle: StateFlow<Vehicle?> = _currentVehicle.asStateFlow()

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _fillUps = MutableStateFlow<List<FillUp>>(emptyList())
    val fillUps: StateFlow<List<FillUp>> = _fillUps.asStateFlow()

    val estimatedFuelRemaining = fuelStateUseCase.estimatedFuelRemaining
    val confidence = fuelStateUseCase.confidence
    val confidencePercent = fuelStateUseCase.confidencePercent
    val safeRange = fuelStateUseCase.safeRange


    // Fuel Rescue integration
    private val _recommendations = MutableStateFlow<List<FuelStationRecommendation>>(emptyList())
    val recommendations: StateFlow<List<FuelStationRecommendation>> = _recommendations.asStateFlow()

    private val _isRefreshingRescue = MutableStateFlow(false)
    val isRefreshingRescue = _isRefreshingRescue.asStateFlow()

    init {
        // Automatically set current vehicle to the first one available
        viewModelScope.launch {
            vehicles.collect { list ->
                if (list.isNotEmpty() && _currentVehicle.value == null) {
                    selectVehicle(list.first())
                }
            }
        }
    }

    fun selectVehicle(vehicle: Vehicle) {
        _currentVehicle.value = vehicle
        viewModelScope.launch {
            launch {
                tripRepository.getTrips(vehicle.id).collect { _trips.value = it }
            }
            launch {
                fillUpRepository.getFillUps(vehicle.id).collect { _fillUps.value = it }
            }
        }
    }

    fun createVehicle(
        year: Int,
        make: String,
        model: String,
        trim: String?,
        color: String?,
        engine: String,
        displacementLiters: Double?,
        cylinderCount: Long?,
        tankCapacity: Double,
        factoryCityMpg: Double,
        factoryHwyMpg: Double,
        preferredFuelType: FuelType,
        preferredFuelGrade: String?,
        unitSystem: UnitSystem,
        reserveFuel: Double,
        lowFuelThreshold: Double
    ) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val initialMpg = 0.55 * factoryCityMpg + 0.45 * factoryHwyMpg
            val newVehicle = Vehicle(
                id = id,
                year = year,
                make = make,
                model = model,
                trim = trim,
                color = color,
                engine = engine,
                engineDisplacementLiters = displacementLiters,
                cylinderCount = cylinderCount,
                tankCapacity = tankCapacity,
                factoryCityMpg = factoryCityMpg,
                factoryHwyMpg = factoryHwyMpg,
                learnedMpg = maxOf(1.0, initialMpg),
                preferredFuelType = preferredFuelType,
                preferredFuelGrade = preferredFuelGrade,
                unitSystem = unitSystem,
                reserveFuelGallons = reserveFuel,
                lowFuelThresholdPercent = lowFuelThreshold
            )
            vehicleRepository.saveVehicle(newVehicle)
            selectVehicle(newVehicle)
        }
    }

    fun logFillUp(gallons: Double, price: Double, odometer: Double?, isFull: Boolean) {
        val vehicle = _currentVehicle.value ?: return
        viewModelScope.launch {
            val fillUp = FillUp(
                id = UUID.randomUUID().toString(),
                vehicleId = vehicle.id,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                gallonsAdded = gallons,
                price = price,
                odometer = odometer,
                isFull = isFull
            )
            fillUpRepository.saveFillUp(fillUp)
            
            // Recalibrate MPG if full
            if (isFull) {
                val segment = calibrationEngine.calibrate(vehicle, fillUp, _fillUps.value)
                if (segment != null) {
                    // Update vehicle in memory to reflect new learned MPG.
                    // The CalibrationEngine updates the repository already.
                    val updatedVehicle = vehicleRepository.getVehicleById(vehicle.id)
                    if (updatedVehicle != null) {
                        _currentVehicle.value = updatedVehicle
                    }
                }
            }
        }
    }

    fun logTrip(distance: Double, durationSeconds: Long, idleTimeSeconds: Long, type: DrivingType) {
        val vehicle = _currentVehicle.value ?: return
        viewModelScope.launch {
            val fuelBurned = FuelEngine.estimateFuelBurned(
                distance = Miles(distance),
                durationSeconds = durationSeconds,
                idleTimeSeconds = idleTimeSeconds,
                drivingType = type,
                displacementLiters = vehicle.engineDisplacementLiters,
                cylinderCount = vehicle.cylinderCount,
                learnedMpg = MilesPerGallon(vehicle.learnedMpg),
                factoryCityMpg = vehicle.factoryCityMpg,
                factoryHwyMpg = vehicle.factoryHwyMpg
            )

            val trip = Trip(
                id = UUID.randomUUID().toString(),
                vehicleId = vehicle.id,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                distance = distance,
                duration = durationSeconds,
                idleTime = idleTimeSeconds,
                averageSpeed = if (durationSeconds > 0) (distance / (durationSeconds / 3600.0)) else 0.0,
                drivingType = type,
                fuelBurned = fuelBurned.value
            )
            tripRepository.saveTrip(trip)
        }
    }

    fun refreshRescue(latitude: Double, longitude: Double, force: Boolean) {
        val vehicle = _currentVehicle.value ?: return
        viewModelScope.launch {
            _isRefreshingRescue.value = true
            try {
                // Fetch nearby stations
                val stations = fuelStationRepository.refreshStations(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMiles = 15.0,
                    fuelType = vehicle.preferredFuelType,
                    forceRefresh = force
                )

                // Simple simulated route distance mapping
                val routeMap = stations.associate { station ->
                    // Simulate routing distance = straight line distance * 1.25 + 0.3
                    val routeDist = station.distanceMiles * 1.25 + 0.3
                    val duration = routeDist / 35.0 * 60.0 // assume 35mph average en route
                    station.id to Pair(routeDist, duration)
                }

                val targetFill = Gallons(vehicle.tankCapacity - estimatedFuelRemaining.value.value)
                
                val evaluated = FuelRescueEngine.evaluateStations(
                    estimatedRemaining = estimatedFuelRemaining.value,
                    learnedMpg = MilesPerGallon(vehicle.learnedMpg),
                    confidenceLevel = confidence.value,
                    vehicleFuelType = vehicle.preferredFuelType,
                    vehicleFuelGradeKey = vehicle.preferredFuelGrade ?: "regular",
                    reserveFuel = Gallons(vehicle.reserveFuelGallons),
                    tankCapacity = Gallons(vehicle.tankCapacity),
                    candidates = stations,
                    routeDistances = routeMap,
                    fallbackPrice = null
                )
                
                _recommendations.value = evaluated
            } catch (e: Exception) {
                // failed to fetch
            } finally {
                _isRefreshingRescue.value = false
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            vehicleRepository.deleteVehicle(vehicleId)
            if (_currentVehicle.value?.id == vehicleId) {
                _currentVehicle.value = null
                _trips.value = emptyList()
                _fillUps.value = emptyList()
            }
        }
    }
}
