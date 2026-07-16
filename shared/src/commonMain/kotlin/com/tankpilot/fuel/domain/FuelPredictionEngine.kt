package com.tankpilot.fuel.domain

import com.tankpilot.trip.domain.DrivingPattern
import kotlinx.coroutines.flow.StateFlow

class FuelPredictionEngine(
    val mpgEstimator: MpgEstimator,
    val activeFuelBurnUseCase: ActiveFuelBurnUseCase
) {
    val activeFuelBurn: StateFlow<Double> = activeFuelBurnUseCase.activeFuelBurn
}
