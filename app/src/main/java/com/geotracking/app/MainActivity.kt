package com.geotracking.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.geotracking.app.service.LocationService

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private var isTracking = false

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackgroundLocation()
                } else {
                    startTracking()
                }
            }
            else -> showPermissionDeniedDialog()
        }
    }

    private val backgroundLocationRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startTracking()
        else showPermissionDeniedDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        toggleButton = findViewById(R.id.toggleButton)

        toggleButton.setOnClickListener {
            if (isTracking) stopTracking() else checkPermissionsAndStart()
        }
    }

    private fun checkPermissionsAndStart() {
        when {
            hasLocationPermission() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundPermission()) {
                    requestBackgroundLocation()
                } else {
                    startTracking()
                }
            }
            else -> locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun requestBackgroundLocation() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de ubicación en background")
            .setMessage("Para rastrear tu ubicación en todo momento, necesitamos acceso a la ubicación en background. Selecciona 'Permitir siempre'.")
            .setPositiveButton("Ir a configuración") { _, _ ->
                backgroundLocationRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            .show()
    }

    private fun startTracking() {
        val intent = Intent(this, LocationService::class.java)
        startForegroundService(intent)
        isTracking = true
        statusText.text = "✅ Rastreo activo\nEnviando ubicación al servidor"
        toggleButton.text = "Detener rastreo"
    }

    private fun stopTracking() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
        isTracking = false
        statusText.text = "⏹ Rastreo detenido"
        toggleButton.text = "Iniciar rastreo"
    }

    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun hasBackgroundPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    } else true

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso requerido")
            .setMessage("La aplicación necesita acceso a la ubicación para funcionar.")
            .setPositiveButton("Configuración") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}