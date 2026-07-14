package com.tankpilot.android.auto

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

private const val TAG = "TankPilotAuto"

/**
 * Android Auto entry point (category POI — see phases/phase-03a-android-auto-foundation.md).
 * Host validation follows the pattern in Google's official car-samples: debuggable
 * builds allow any host (DHU, unsigned test builds), release builds are restricted
 * to the Car App Library's own bundled allowlist of legitimate Android Auto /
 * Android Automotive OS host certificates.
 *
 * Temporary diagnostic logging (Phase 3A.5): the host never even attempted to
 * resolve/bind this service in logcat history on the test device, so these logs
 * exist to prove definitively, on the next real connection attempt, whether the
 * process ever reaches this class at all. Filter with: adb logcat -s TankPilotAuto
 */
class TankPilotCarAppService : CarAppService() {

    init {
        Log.d(TAG, "TankPilotCarAppService instantiated")
    }

    override fun createHostValidator(): HostValidator {
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Log.d(TAG, "createHostValidator() called — isDebuggable=$isDebuggable")
        return if (isDebuggable) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(applicationContext)
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build()
        }
    }

    override fun onCreateSession(): Session {
        Log.d(TAG, "onCreateSession() called")
        return TankPilotCarSession()
    }
}
