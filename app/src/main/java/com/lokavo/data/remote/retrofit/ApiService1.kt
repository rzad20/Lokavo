package com.lokavo.data.remote.retrofit

import com.lokavo.data.remote.request.ModelingResultsRequest
import com.lokavo.data.remote.request.PlaceDetailsRequest
import com.lokavo.data.remote.response.ArticleResponse
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.data.remote.response.PlaceDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService1 {
    @POST("/modelling_results")
    suspend fun getModelingResults(
        @Body request: ModelingResultsRequest
    ): ModelingResultsResponse

    @POST("/competitor_details")
    suspend fun getPlaceDetail(
        @Body request: PlaceDetailsRequest
    ): PlaceDetailsResponse

    @GET("articles")
    suspend fun getArticles(): ArticleResponse
}