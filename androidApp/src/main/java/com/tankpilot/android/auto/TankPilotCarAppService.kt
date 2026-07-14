package com.tankpilot.android.auto

import android.content.pm.ApplicationInfo
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

/**
 * Android Auto entry point (category POI — see phases/phase-03a-android-auto-foundation.md).
 * Host validation follows the pattern in Google's official car-samples: debuggable
 * builds allow any host (DHU, unsigned test builds), release builds are restricted
 * to the Car App Library's own bundled allowlist of legitimate Android Auto /
 * Android Automotive OS host certificates.
 */
class TankPilotCarAppService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(applicationContext)
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build()
        }
    }

    override fun onCreateSession(): Session {
        return TankPilotCarSession()
    }
}
