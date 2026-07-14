package com.tankpilot.android.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

enum class BluetoothPermissionState {
    UNKNOWN,
    GRANTED,
    DENIED
}

@Composable
fun rememberBluetoothPermissionState(): Pair<BluetoothPermissionState, () -> Unit> {
    val context = LocalContext.current
    var permissionState by remember { mutableStateOf(checkPermissions(context)) }

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        permissionState = if (allGranted) BluetoothPermissionState.GRANTED else BluetoothPermissionState.DENIED
    }

    val requestPermissions = {
        if (permissionState != BluetoothPermissionState.GRANTED) {
            launcher.launch(permissionsToRequest)
        }
    }

    return permissionState to requestPermissions
}

private fun checkPermissions(context: Context): BluetoothPermissionState {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val allGranted = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    
    return if (allGranted) BluetoothPermissionState.GRANTED else BluetoothPermissionState.UNKNOWN
}
