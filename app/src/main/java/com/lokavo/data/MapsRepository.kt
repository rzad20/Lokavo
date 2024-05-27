package com.lokavo.data

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lokavo.data.retrofit.ApiService
import retrofit2.HttpException
import com.lokavo.data.response.MapsResponse
import com.lokavo.data.response.PlacesItem
import com.lokavo.data.retrofit.ArgLatLong
import com.lokavo.data.retrofit.PlaceId

class MapsRepository private constructor(private var apiService: ApiService) {
    fun getNearbyPlace(latitude: Double, longitude: Double) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNearbyPlace(ArgLatLong(latitude, longitude))
            val places = response.places
            if (places != null) {
                if (places.isEmpty()) {
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
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, MapsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        }
    }

    fun getPlaceDetail(placeId: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getPlaceDetail(PlaceId(placeId))
            val places = response.places
            if (!places.isNullOrEmpty()) {
                val place = places[0]
                val placeItem = PlacesItem(
                    place?.averageHour,
                    place?.mostPopularTimes,
                    place?.address,
                    place?.rating,
                    place?.coordinates,
                    place?.stdHour,
                    place?.avgPopularity,
                    place?.featuredImage,
                    place?.nearestCompetitorTopHourPopularity,
                    place?.reviews,
                    place?.reviewsPerRating,
                    place?.topHourPopularity,
                    place?.name,
                    place?.mainCategory,
                    place?.nearestCompetitorDistance,
                    place?.nearestCompetitorTopAveragePopularity,
                    place?.categories,
                    place?.placeId,
                    place?.topAveragePopularity,
                    place?.nearestCompetitorPlaceId
                )
                emit(Result.Success(placeItem))
            } else {
                emit(Result.Empty)
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, MapsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
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