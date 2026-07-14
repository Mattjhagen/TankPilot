package com.tankpilot.location.data

import com.tankpilot.location.domain.HeadingProvider
import com.tankpilot.location.domain.HeadingSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Production provider for when no compass or location heading source is available.
 * Emits null permanently. The dashboard will display — for heading.
 */
class UnavailableHeadingProvider : HeadingProvider {
    override val heading: StateFlow<HeadingSample?> = MutableStateFlow(null)
}
