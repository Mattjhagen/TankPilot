package com.tankpilot.trip.domain

import com.tankpilot.location.domain.LocationSample
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TripRouteRecorder(
    private val tripRepository: TripRepository,
    private val scope: CoroutineScope
) {
    private val _route = MutableStateFlow<List<LocationSample>>(emptyList())
    val route: StateFlow<List<LocationSample>> = _route.asStateFlow()

    private val mutex = Mutex()
    private var lastSavedIndex = 0
    private var activeTripId: String? = null

    // Filtering config
    private val minDistanceMeters = 5.0
    private val minTimeMs = 2000L
    private val maxAccuracyMeters = 30.0

    suspend fun startRecording(tripId: String) = mutex.withLock {
        activeTripId = tripId
        _route.value = emptyList()
        lastSavedIndex = 0
    }

    suspend fun onLocationSample(sample: LocationSample) = mutex.withLock {
        if (activeTripId == null) return@withLock
        
        // Filtering
        val hAcc = sample.horizontalAccuracyMeters
        if (hAcc == null || hAcc > maxAccuracyMeters) return@withLock

        val currentRoute = _route.value
        val lastSample = currentRoute.lastOrNull()
        
        if (lastSample != null) {
            // Check out of order or duplicate
            if (sample.timestamp.toEpochMilliseconds() <= lastSample.timestamp.toEpochMilliseconds()) return@withLock
            
            // Time filter
            val timeDiffMs = sample.timestamp.toEpochMilliseconds() - lastSample.timestamp.toEpochMilliseconds()
            if (timeDiffMs < minTimeMs) return@withLock
            
            // Distance filter (haversine)
            val distance = calculateDistanceMeters(
                lastSample.latitude, lastSample.longitude,
                sample.latitude, sample.longitude
            )
            if (distance < minDistanceMeters) return@withLock
        }

        _route.value = currentRoute + sample
        
        // Save in batches of 50 to avoid huge memory/db spikes
        val batchSize = 50
        if (_route.value.size - lastSavedIndex >= batchSize) {
            savePendingPoints()
        }
    }

    private suspend fun savePendingPoints() {
        val tripId = activeTripId ?: return
        val currentRoute = _route.value
        val pointsToSave = currentRoute.subList(lastSavedIndex, currentRoute.size)
        if (pointsToSave.isNotEmpty()) {
            val startIndex = lastSavedIndex
            lastSavedIndex = currentRoute.size
            // Fire and forget so we don't block the location thread too long
            scope.launch {
                tripRepository.saveTripRoutePoints(tripId, pointsToSave, startIndex)
            }
        }
    }

    suspend fun stopAndFinalize(): Pair<List<LocationSample>, Int> = mutex.withLock {
        val currentRoute = _route.value
        val pointsToSave = currentRoute.subList(lastSavedIndex, currentRoute.size)
        val startIndex = lastSavedIndex
        
        lastSavedIndex = currentRoute.size
        activeTripId = null
        
        return@withLock Pair(pointsToSave, startIndex)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = (lat2 - lat1) * kotlin.math.PI / 180.0
        val dLon = (lon2 - lon1) * kotlin.math.PI / 180.0
        val aLat = lat1 * kotlin.math.PI / 180.0
        val bLat = lat2 * kotlin.math.PI / 180.0

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2) * kotlin.math.cos(aLat) * kotlin.math.cos(bLat)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
}
