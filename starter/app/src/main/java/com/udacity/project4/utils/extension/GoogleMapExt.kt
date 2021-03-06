package com.udacity.project4.utils.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RawRes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

private const val TAG: String = "GoogleMapExt"

private enum class ZoomLevel(val value: Float) {
    WORLD(1f),
    LAND_MASS(5f),
    CITY(10f),
    STREET(15f),
    BUILDINGS(20f);
}

@SuppressLint("MissingPermission")
fun GoogleMap.enableLocation() {
    isMyLocationEnabled = true
}

fun GoogleMap.setCurrentLocation(location: Location) {
    val latLng = LatLng(location.latitude, location.longitude)
    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZoomLevel.STREET.value))
}

fun GoogleMap.setStyle(context: Context, @RawRes style: Int) {
    try {
        val success = setMapStyle(MapStyleOptions.loadRawResourceStyle(context, style))
        if (!success) {
            Log.e(TAG, "Style parsing failed.")
        }
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
}

fun GoogleMap.onLongClick(block: (latLng: LatLng) -> Unit) {
    setOnMapLongClickListener { latLng: LatLng ->
        block(latLng)
    }
}

fun GoogleMap.addLatLngMarker(title: String, latLng: LatLng) {
    val snippet = String.format(
        Locale.getDefault(),
        "Lat: %1$.5f, Long: %2$.5f",
        latLng.latitude,
        latLng.longitude
    )
    val markerOptions = MarkerOptions()
        .position(latLng)
        .title(title)
        .snippet(snippet)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

    addMarker(markerOptions)
}