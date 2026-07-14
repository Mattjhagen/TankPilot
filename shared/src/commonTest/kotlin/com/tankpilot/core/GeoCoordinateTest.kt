package com.tankpilot.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeoCoordinateTest {

    @Test
    fun validCoordinatePasses() {
        assertTrue(GeoCoordinate.isValid(37.7749, -122.4194))
        assertNotNull(GeoCoordinate.validOrNull(37.7749, -122.4194))
    }

    @Test
    fun nanCoordinateFails() {
        assertFalse(GeoCoordinate.isValid(Double.NaN, -122.4194))
        assertFalse(GeoCoordinate.isValid(37.7749, Double.NaN))
        assertNull(GeoCoordinate.validOrNull(Double.NaN, Double.NaN))
    }

    @Test
    fun infiniteCoordinateFails() {
        assertFalse(GeoCoordinate.isValid(Double.POSITIVE_INFINITY, -122.4194))
        assertFalse(GeoCoordinate.isValid(37.7749, Double.NEGATIVE_INFINITY))
    }

    @Test
    fun outOfRangeCoordinateFails() {
        assertFalse(GeoCoordinate.isValid(91.0, 0.0))
        assertFalse(GeoCoordinate.isValid(-91.0, 0.0))
        assertFalse(GeoCoordinate.isValid(0.0, 181.0))
        assertFalse(GeoCoordinate.isValid(0.0, -181.0))
    }

    @Test
    fun nullIslandSentinelFails() {
        assertFalse(GeoCoordinate.isValid(0.0, 0.0))
        assertNull(GeoCoordinate.validOrNull(0.0, 0.0))
    }
}
