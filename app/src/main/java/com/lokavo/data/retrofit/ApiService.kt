package com.lokavo.data.retrofit

import com.lokavo.data.response.MapsResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class NearbyPlaceRequest(
    val arglatitude: Double,
    val arglongitude: Double
)

interface ApiService {
    @POST("/second_try")
    suspend fun getNearbyPlace(
        @Body request: NearbyPlaceRequest
    ): MapsResponse
}