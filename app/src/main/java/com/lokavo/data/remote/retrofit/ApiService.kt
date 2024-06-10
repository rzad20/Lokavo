package com.lokavo.data.remote.retrofit

import com.lokavo.data.remote.request.ArgLatLong
import com.lokavo.data.remote.request.PlaceId
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.data.remote.response.PlaceDetailsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/modelling_results")
    suspend fun getModelingResults(
        @Body request: ArgLatLong
    ): ModelingResultsResponse

    @POST("/competitor_details")
    suspend fun getPlaceDetail(
        @Body request: PlaceId
    ): PlaceDetailsResponse
}