package com.udacity.project4.utils

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.udacity.project4.R
import java.util.concurrent.TimeUnit

const val GEOFENCE_RADIUS_IN_METERS = 100f
val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)

fun errorMessage(context: Context, errorCode: Int): String = context.resources.getString(
    when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> R.string.geofence_not_available
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> R.string.geofence_too_many_geofences
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> R.string.geofence_too_many_pending_intents
        else -> R.string.geofence_unknown_error
    }
)