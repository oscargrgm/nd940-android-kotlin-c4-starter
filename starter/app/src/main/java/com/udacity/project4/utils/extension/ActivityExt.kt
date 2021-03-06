package com.udacity.project4.utils.extension

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class CurrentLocationResult {
    data class Success(val value: Location) : CurrentLocationResult()
    data class Error(val exception: Throwable) : CurrentLocationResult()
}

inline fun <reified T : Activity> Activity.launchActivityAndFinish(extras: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        extras?.let { putExtras(it) }
    }
    startActivity(intent)
    finish()
}

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
suspend fun Activity.requireCurrentLocation(): CurrentLocationResult =
    suspendCoroutine { continuation ->
        LocationServices.getFusedLocationProviderClient(this).lastLocation.run {
            addOnSuccessListener { continuation.resume(CurrentLocationResult.Success(it)) }
            addOnFailureListener { continuation.resume(CurrentLocationResult.Error(it)) }
        }
    }