package com.tankpilot.dashboard.domain

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*

class DashboardSessionStateTest {

    @Test
    fun `test DashboardSessionState serializes and deserializes correctly`() {
        val original = DashboardSessionState(
            isVisible = true,
            enteredAutomatically = true,
            startTimeEpochMs = 123456789L,
            isFocusModeEnabled = true,
            theme = DashboardTheme.NIGHT
        )

        val jsonString = Json.encodeToString(original)
        val deserialized = Json.decodeFromString<DashboardSessionState>(jsonString)

        assertEquals(original, deserialized)
    }

    @Test
    fun `test DashboardSessionState defaults`() {
        val jsonString = "{}"
        val deserialized = Json.decodeFromString<DashboardSessionState>(jsonString)
        
        assertFalse(deserialized.isVisible)
        assertFalse(deserialized.enteredAutomatically)
        assertEquals(0L, deserialized.startTimeEpochMs)
        assertFalse(deserialized.isFocusModeEnabled)
        assertEquals(DashboardTheme.ADAPTIVE, deserialized.theme)
    }
}
