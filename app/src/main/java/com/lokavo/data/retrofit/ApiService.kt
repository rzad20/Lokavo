package com.lokavo.data.retrofit

import com.lokavo.data.response.MapsResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class ArgLatLong(
    val arglatitude: Double,
    val arglongitude: Double
)

data class PlaceId(
    val argplace_id: String
)

interface ApiService {
    @POST("/v1")
    suspend fun getNearbyPlace(
        @Body request: ArgLatLong
    ): MapsResponse

    @POST("v2")
    suspend fun getPlaceDetail(
        @Body request: PlaceId
    ): MapsResponse
}