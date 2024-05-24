package com.lokavo.data

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lokavo.data.retrofit.ApiService
import retrofit2.HttpException
import com.lokavo.data.response.MapsResponse
import com.lokavo.data.response.PlacesItem
import com.lokavo.data.retrofit.NearbyPlaceRequest

class MapsRepository private constructor(private var apiService: ApiService) {
    fun getNearbyPlace(latitude: Double, longitude: Double) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNearbyPlace(NearbyPlaceRequest(latitude, longitude))
            val places = response.places
            if (places != null) {
                if (places.isEmpty()) {
                    emit(Result.Empty)
                } else {
                    val storyList = places.map { place ->
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
                            place?.name,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                        )
                    }
                    emit(Result.Success(storyList))
                }
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