package com.tankpilot.trip.domain

import com.tankpilot.core.FakeAppClock
import com.tankpilot.location.domain.SpeedSource
import com.tankpilot.telemetry.domain.TelemetryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
import kotlin.test.*

class SpeedSelectionTest {

    private lateinit var clock: FakeAppClock
    private lateinit var telemetryFlow: MutableStateFlow<TelemetryData>
    private lateinit var gpsSpeedFlow: MutableStateFlow<Double?>
    private lateinit var scope: CoroutineScope
    private lateinit var useCase: SpeedSelectionUseCase

    @BeforeTest
    fun setUp() {
        clock = FakeAppClock(Instant.fromEpochSeconds(1000L))
        telemetryFlow = MutableStateFlow(TelemetryData())
        gpsSpeedFlow = MutableStateFlow(null)
        scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
        
        useCase = SpeedSelectionUseCase(
            telemetryFlow = telemetryFlow.asStateFlow(),
            gpsSpeedFlow = gpsSpeedFlow.asStateFlow(),
            clock = clock,
            scope = scope,
            freshnessThresholdMs = 2000L
        )
    }

    @Test
    fun testDefaultStateIsUnknown() {
        val selected = useCase.selectedSpeed.value
        assertNull(selected.valueKmh)
        assertEquals(SpeedSource.UNKNOWN, selected.source)
        assertFalse(selected.isFresh)
    }

    @Test
    fun testGpsSpeedFallbackWhenObdUnavailable() {
        gpsSpeedFlow.value = 65.0
        val selected = useCase.selectedSpeed.value
        assertEquals(65.0, selected.valueKmh)
        assertEquals(SpeedSource.GPS, selected.source)
        assertTrue(selected.isFresh)
    }

    @Test
    fun testObdSpeedTakesPrecedenceWhenFresh() {
        gpsSpeedFlow.value = 65.0
        telemetryFlow.value = TelemetryData(speedKmh = 70.0)
        
        val selected = useCase.selectedSpeed.value
        assertEquals(70.0, selected.valueKmh)
        assertEquals(SpeedSource.OBD, selected.source)
        assertTrue(selected.isFresh)
    }

    @Test
    fun testObdSpeedStaleFallsBackToGps() {
        gpsSpeedFlow.value = 65.0
        telemetryFlow.value = TelemetryData(speedKmh = 70.0)
        assertEquals(70.0, useCase.selectedSpeed.value.valueKmh)

        // Advance clock past 2 seconds (3 seconds later)
        clock.currentTime = Instant.fromEpochSeconds(1003L)
        
        // Trigger flow update by changing GPS speed to simulate updates
        gpsSpeedFlow.value = 66.0

        val selected = useCase.selectedSpeed.value
        assertEquals(66.0, selected.valueKmh)
        assertEquals(SpeedSource.GPS, selected.source)
        assertTrue(selected.isFresh)
    }
}
