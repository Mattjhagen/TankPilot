package com.tankpilot.fuel.domain

import com.tankpilot.fuel.FuelEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class ActiveFuelBurnUseCase(
    private val distanceDrivenMiles: StateFlow<Double>,
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
        distanceDrivenMiles,
        idleTimeSeconds,
        mpgFlow,
        _idleRateMultiplier
    ) { distance, idleSeconds, mpgEstimate, multiplier ->
        val mpgVal = mpgEstimate.value ?: 25.0
        val drivingBurn = if (mpgVal > 0.0) distance / mpgVal else 0.0
        
        val idleHours = idleSeconds / 3600.0
        val idleBurn = baseIdleRateGph * multiplier * idleHours
        
        drivingBurn + idleBurn
    }.stateIn(scope, SharingStarted.Eagerly, 0.0)
}
