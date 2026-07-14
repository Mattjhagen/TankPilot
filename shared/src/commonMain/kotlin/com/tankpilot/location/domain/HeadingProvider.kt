package com.tankpilot.location.domain

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

data class HeadingSample(
    val degrees: Double,
    val cardinalDirection: String,
    val accuracy: Double,
    val timestamp: Instant,
    val source: String
)

interface HeadingProvider {
    val heading: StateFlow<HeadingSample?>
}
