package com.example.geofenceapp.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            val errorCode = geofencingEvent.errorCode
            Log.e("GeofenceReceiver", "Geofencing error: $errorCode")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val transitionText = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
            else -> "UNKNOWN"
        }

        Toast.makeText(
            context,
            "Geofence event: $transitionText",
            Toast.LENGTH_LONG
        ).show()
    }
}