package com.example.geofenceapp.helper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

fun geofencePendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, GeofenceBroadcastReceiver::class.java)

    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

    return PendingIntent.getBroadcast(
        context,
        0,
        intent,
        flags
    )
}
