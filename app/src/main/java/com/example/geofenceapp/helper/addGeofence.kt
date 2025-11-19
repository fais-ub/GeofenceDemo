package com.example.geofenceapp.helper

import android.content.Context
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.geofenceapp.GEOFENCE_ID
import com.example.geofenceapp.GEOFENCE_LATITUDE
import com.example.geofenceapp.GEOFENCE_LONGITUDE
import com.example.geofenceapp.GEOFENCE_RADIUS_IN_METERS
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.GeofenceStatusCodes
import android.util.Log

fun addSingleGeofence(
    context: Context,
    geofencingClient: GeofencingClient
) {
    // Safety check permission
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocation) {
        Toast.makeText(
            context,
            "Izin lokasi belum diberikan",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val geofence = Geofence.Builder()
        .setRequestId(GEOFENCE_ID)
        .setCircularRegion(
            GEOFENCE_LATITUDE,
            GEOFENCE_LONGITUDE,
            GEOFENCE_RADIUS_IN_METERS
        )
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(
            Geofence.GEOFENCE_TRANSITION_ENTER or
                    Geofence.GEOFENCE_TRANSITION_EXIT
        )
        .build()

    val geofencingRequest = GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .addGeofence(geofence)
        .build()

    val pendingIntent = geofencePendingIntent(context)

    geofencingClient.addGeofences(geofencingRequest, pendingIntent)
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Geofence diaktifkan",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener { e ->
            val message = if (e is ApiException) {
                when (e.statusCode) {
                    GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE ->
                        "Gagal: layanan lokasi tidak tersedia atau dimatikan"
                    GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES ->
                        "Gagal: terlalu banyak geofence terdaftar"
                    GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION ->
                        "Gagal: izin lokasi (termasuk background) belum lengkap"
                    else ->
                        "Gagal (kode ${e.statusCode})"
                }
            } else {
                "Gagal: ${e.localizedMessage ?: "unknown error"}"
            }

            Log.e("Geofence", "Error menambahkan geofence", e)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
}
