package com.lokavo.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lokavo.R
import com.lokavo.data.Result
import com.lokavo.data.remote.request.ArgLatLong
import com.lokavo.data.remote.request.PlaceId
import com.lokavo.data.remote.response.ClusterProportion
import com.lokavo.data.remote.response.DetailsItem
import com.lokavo.data.remote.retrofit.ApiService
import retrofit2.HttpException
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.data.remote.response.PlaceDetailsResponse
import com.lokavo.data.remote.response.PoiMapItem

class MapsRepository private constructor(private var apiService: ApiService) {

    data class NearbyPlaceResult(
        val placeList: List<PoiMapItem>,
        val summaryHeader: String?,
        val shortInterpretation: String?,
        val longInterpretation: String?,
        val clusterProportion: ClusterProportion?
    )


    fun getModelingResults(latitude: Double, longitude: Double) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getModelingResults(ArgLatLong(latitude, longitude))
            val places = response.poiMap
            val summaryHeader = response.summaryHeader
            val shortInterpretation = response.shortInterpretation
            val longInterpretation = response.longInterpretation
            val clusterProportion = response.clusterProportion
            if (places != null) {
                if (places.isEmpty()) {
                    emit(Result.Empty)
                } else {
                    val placeList = places.map { place ->
                        PoiMapItem(
                            place?.cluster,
                            place?.placeId,
                            place?.coordinates
                        )
                    }
                    val result = ModelingResultsResponse(
                        poiMap = placeList,
                        summaryHeader = summaryHeader,
                        shortInterpretation = shortInterpretation,
                        longInterpretation = longInterpretation,
                        clusterProportion = clusterProportion
                    )
                    emit(Result.Success(result))
                }
            } else {
                emit(Result.Error(response.message ?: R.string.not_found.toString()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ModelingResultsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: Exception) {
            emit(e.message?.let { Result.Error(it) })
        }
    }

    fun getPlaceDetail(placeId: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getPlaceDetail(PlaceId(placeId))
            val details = response.details
            if (details != null) {
                if (details.isEmpty()) {
                    emit(Result.Empty)
                } else {
                    val detail = details[0]
                    val detailItem = DetailsItem(
                        detail?.placeId,
                        detail?.name,
                        detail?.address,
                        detail?.featuredImage,
                        detail?.reviews,
                        detail?.mainCategory,
                        detail?.categories,
                        detail?.rating,
                        detail?.reviewsPerRating,
                        detail?.coordinates,
                        detail?.mostPopularTimes,
                        detail?.averageHour,
                        detail?.stdHour,
                        detail?.avgPopularity,
                        detail?.topHourPopularity,
                        detail?.topAveragePopularity,
                        detail?.nearestCompetitorPlaceId,
                        detail?.nearestCompetitorDistance,
                        detail?.nearestCompetitorTopHourPopularity,
                        detail?.nearestCompetitorTopAveragePopularity,
                    )
                    emit(Result.Success(detailItem))
                }
            } else {
                emit(Result.Error(response.message ?: R.string.not_found.toString()))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, PlaceDetailsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: Exception) {
            emit(Result.Error("Terjadi Kesalahan, Silahkan Coba Lagi"))
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

