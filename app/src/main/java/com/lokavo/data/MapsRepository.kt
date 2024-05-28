package com.lokavo.data

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lokavo.R
import com.lokavo.data.retrofit.ApiService
import retrofit2.HttpException
import com.lokavo.data.response.MapsResponse
import com.lokavo.data.response.PlacesItem
import com.lokavo.data.retrofit.ArgLatLong
import com.lokavo.data.retrofit.PlaceId
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MapsRepository private constructor(private var apiService: ApiService) {
    fun getNearbyPlace(latitude: Double, longitude: Double) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNearbyPlace(ArgLatLong(latitude, longitude))
            val places = response.places
            if (places.isNullOrEmpty()) {
                emit(Result.Empty)
            } else {
                val placeList = places.map { place ->
                    PlacesItem(
                        null,
                        null,
                        null,
                        null,
                        place?.coordinates,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        place?.placeId,
                        null,
                        null,
                    )
                }
                emit(Result.Success(placeList))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, MapsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    fun getPlaceDetail(placeId: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getPlaceDetail(PlaceId(placeId))
            val details = response.details
            if (details.isNullOrEmpty()) {
                emit(Result.Empty)
            } else {
                val detail = details[0]
                val detailItem = PlacesItem(
                    detail?.averageHour,
                    detail?.mostPopularTimes,
                    detail?.address,
                    detail?.rating,
                    detail?.coordinates,
                    detail?.stdHour,
                    detail?.avgPopularity,
                    detail?.featuredImage,
                    detail?.nearestCompetitorTopHourPopularity,
                    detail?.reviews,
                    detail?.reviewsPerRating,
                    detail?.topHourPopularity,
                    detail?.name,
                    detail?.mainCategory,
                    detail?.nearestCompetitorDistance,
                    detail?.nearestCompetitorTopAveragePopularity,
                    detail?.categories,
                    detail?.placeId,
                    detail?.topAveragePopularity,
                    detail?.nearestCompetitorPlaceId
                )
                emit(Result.Success(detailItem))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, MapsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: Exception) {
            emit(Result.Error("An error occurred: ${e.message}"))
        }
    }

    companion object {
        @Volatile
        private var instance: MapsRepository? = null
        fun getInstance(
            apiService: ApiService
        ) =
            instance ?: synchronized(this) {
                instance ?: MapsRepository(apiService)
            }.also { instance = it }
    }
}