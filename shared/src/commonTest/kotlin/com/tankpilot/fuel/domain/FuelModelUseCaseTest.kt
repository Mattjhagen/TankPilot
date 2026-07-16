package com.tankpilot.fuel.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.test.*

class FuelModelUseCaseTest {

    private class FakeVehicleEfficiencyProvider : VehicleEfficiencyProvider {
        override val currentFactoryCityMpg = MutableStateFlow<Double?>(20.0).asStateFlow()
        override val currentFactoryHighwayMpg = MutableStateFlow<Double?>(30.0).asStateFlow()
        override val currentLearnedMpg = MutableStateFlow<Double?>(25.0).asStateFlow()
        override val currentTankCapacityGallons = MutableStateFlow<Double?>(15.0).asStateFlow()
        override val currentReserveFuelGallons = MutableStateFlow<Double?>(2.0).asStateFlow()
        override val currentLowFuelThresholdPercent = MutableStateFlow<Double?>(0.15).asStateFlow()
    }

    private lateinit var scope: CoroutineScope
    private lateinit var persistedFuelRemaining: MutableStateFlow<Double>
    private lateinit var activeFuelBurn: MutableStateFlow<Double>
    private lateinit var confidencePercent: MutableStateFlow<Int>
    private lateinit var useCase: FuelModelUseCase

    @BeforeTest
    fun setUp() {
        scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        persistedFuelRemaining = MutableStateFlow(10.0)
        activeFuelBurn = MutableStateFlow(0.0)
        confidencePercent = MutableStateFlow(90)

        useCase = FuelModelUseCase(
            persistedFuelRemaining = persistedFuelRemaining.asStateFlow(),
            activeFuelBurn = activeFuelBurn.asStateFlow(),
            efficiencyProvider = FakeVehicleEfficiencyProvider(),
            confidencePercent = confidencePercent.asStateFlow(),
            alertEngine = AlertEngine(),
            scope = scope
        )
    }

    @Test
    fun testDisplayedFuelEqualsPersistedWhenNoActiveBurn() {
        assertEquals(10.0, useCase.displayedFuelRemainingGallons.value, absoluteTolerance = 0.001)
    }

    @Test
    fun testActiveFuelBurnLowersDisplayedFuelDuringATrip() {
        activeFuelBurn.value = 2.5
        assertEquals(7.5, useCase.displayedFuelRemainingGallons.value, absoluteTolerance = 0.001)

        activeFuelBurn.value = 4.0
        assertEquals(6.0, useCase.displayedFuelRemainingGallons.value, absoluteTolerance = 0.001)
    }

    @Test
    fun testDisplayedFuelNeverGoesNegative() {
        activeFuelBurn.value = 999.0
        assertEquals(0.0, useCase.displayedFuelRemainingGallons.value, absoluteTolerance = 0.001)
    }

    @Test
    fun testDisplayedFuelPercentReflectsActiveBurn() {
        activeFuelBurn.value = 7.5
        // (10.0 - 7.5) / 15.0 tank capacity
        assertEquals(0.1667, useCase.displayedFuelPercent.value ?: -1.0, absoluteTolerance = 0.001)
    }
}
