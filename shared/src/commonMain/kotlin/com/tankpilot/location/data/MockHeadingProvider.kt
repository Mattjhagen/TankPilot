package com.tankpilot.location.data

import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.location.domain.HeadingSample
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class MockHeadingProvider : HeadingProvider {
    private val _heading = MutableStateFlow<HeadingSample?>(null)
    override val heading: StateFlow<HeadingSample?> = _heading.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            var deg = 0.0
            while(isActive) {
                _heading.value = HeadingSample(
                    degrees = deg,
                    cardinalDirection = "N",
                    accuracy = 5.0,
                    timestamp = Clock.System.now(),
                    source = "MOCK"
                )
                deg = (deg + 1) % 360
                delay(1000)
            }
        }
    }
}
