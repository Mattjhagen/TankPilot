package com.tankpilot.android

import android.Manifest
import android.os.Build
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Start Drive must request POST_NOTIFICATIONS on Android 13+ (API 33, TIRAMISU) since
 * the foreground service notification is otherwise silently suppressed there, but must
 * not request it on older devices where the permission doesn't exist.
 */
class MainActivityPermissionsTest {

    @Test
    fun `requests location permissions on all supported API levels`() {
        val perms = requiredStartDrivePermissions(sdkInt = Build.VERSION_CODES.O)
        assertTrue(perms.contains(Manifest.permission.ACCESS_FINE_LOCATION))
        assertTrue(perms.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @Test
    fun `does not request POST_NOTIFICATIONS below Android 13`() {
        val perms = requiredStartDrivePermissions(sdkInt = Build.VERSION_CODES.S_V2)
        assertFalse(perms.contains(Manifest.permission.POST_NOTIFICATIONS))
    }

    @Test
    fun `requests POST_NOTIFICATIONS on Android 13 and above`() {
        val perms = requiredStartDrivePermissions(sdkInt = Build.VERSION_CODES.TIRAMISU)
        assertTrue(perms.contains(Manifest.permission.POST_NOTIFICATIONS))
    }
}
