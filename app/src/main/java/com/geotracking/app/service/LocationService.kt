package com.geotracking.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.geotracking.app.network.LocationRequest
import com.geotracking.app.network.RetrofitClient
import kotlinx.coroutines.*

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val CHANNEL_ID = "GeoTrackingChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 30000L
        ).setMinUpdateIntervalMillis(15000L).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    serviceScope.launch {
                        sendLocation(location)
                    }
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            stopSelf()
        }
    }

    private suspend fun sendLocation(location: android.location.Location) {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val battery = getBatteryLevel()

        try {
            RetrofitClient.apiService.sendLocation(
                LocationRequest(
                    device_id = deviceId,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    speed = location.speed,
                    altitude = location.altitude,
                    battery_level = battery
                )
            )
        } catch (e: Exception) {
            // Reintentará en el siguiente ciclo
        }
    }

    private fun getBatteryLevel(): Int {
        val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "GeoTracking", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Rastreando ubicación en background" }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GeoTracking activo")
            .setContentText("Enviando ubicación al servidor")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
    }
}