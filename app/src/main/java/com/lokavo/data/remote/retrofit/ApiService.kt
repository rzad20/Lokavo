package com.lokavo.data.remote.retrofit

import com.lokavo.data.remote.request.ArgLatLong
import com.lokavo.data.remote.request.PlaceId
import com.lokavo.data.remote.response.MapsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/competitors_nearby")
    suspend fun getNearbyPlace(
        @Body request: ArgLatLong
    ): MapsResponse

    @POST("/competitor_details")
    suspend fun getPlaceDetail(
        @Body request: PlaceId
    ): MapsResponse
}