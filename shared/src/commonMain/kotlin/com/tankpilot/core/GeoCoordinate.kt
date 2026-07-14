package com.tankpilot.core

data class GeoCoordinate(val latitude: Double, val longitude: Double) {
    companion object {
        /**
         * (0.0, 0.0) is excluded deliberately — it is "null island," the common sentinel
         * a missing/unset GPS fix decays to, not a real destination.
         */
        fun isValid(latitude: Double, longitude: Double): Boolean {
            if (!latitude.isFinite() || !longitude.isFinite()) return false
            if (latitude < -90.0 || latitude > 90.0) return false
            if (longitude < -180.0 || longitude > 180.0) return false
            if (latitude == 0.0 && longitude == 0.0) return false
            return true
        }

        fun validOrNull(latitude: Double, longitude: Double): GeoCoordinate? {
            return if (isValid(latitude, longitude)) GeoCoordinate(latitude, longitude) else null
        }
    }
}
