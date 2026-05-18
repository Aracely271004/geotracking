package com.geotracking.app.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LocationRequest(
    val device_id: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val altitude: Double,
    val battery_level: Int
)

interface ApiService {
    @POST("api/location")
    suspend fun sendLocation(
        @Body location: LocationRequest
    ): Response<Any>
}