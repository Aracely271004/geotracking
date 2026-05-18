package com.geotracking.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.geotracking.app.service.LocationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, LocationService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}