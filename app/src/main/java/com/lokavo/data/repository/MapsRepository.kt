package com.lokavo.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lokavo.R
import com.lokavo.data.Result
import com.lokavo.data.remote.request.ModelingResultsRequest
import com.lokavo.data.remote.request.PlaceDetailsRequest
import com.lokavo.data.remote.response.DetailsItem
import com.lokavo.data.remote.retrofit.ApiService1
import retrofit2.HttpException
import com.lokavo.data.remote.response.ModelingResultsResponse
import com.lokavo.data.remote.response.PlaceDetailsResponse
import com.lokavo.data.remote.response.PoiMapItem
import com.lokavo.utils.wrapEspressoIdlingResource
import java.net.SocketTimeoutException

class MapsRepository private constructor(private var apiService1: ApiService1) {
    fun getModelingResults(latitude: Double, longitude: Double) = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val response = apiService1.getModelingResults(ModelingResultsRequest(latitude, longitude))
                val places = response.poiMap
                val summaryHeader = response.summaryHeader
                val longInterpretation = response.longInterpretation
                val clusterProportion = response.clusterProportion
                val clusterInterpretation = response.clusterInterpretation
                if (places != null) {
                    if (places.isEmpty()) {
                        emit(Result.Empty)
                    } else {

                        val placeList = places.map { place ->
                            PoiMapItem(
                                place?.cluster,
                                place?.placeId,
                                place?.coordinates,
                            )
                        }
                        val filteredPlaces = places.filter { it?.top != 0 }

                        val result = ModelingResultsResponse(
                            poiMap = placeList,
                            summaryHeader = summaryHeader,
                            longInterpretation = longInterpretation,
                            clusterProportion = clusterProportion,
                            clusterInterpretation = clusterInterpretation,
                            top = filteredPlaces
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
            } catch (e: SocketTimeoutException) {
                emit(Result.Error("Request Timeout"))
            } catch (e: Exception) {
                emit(Result.Error("Terjadi Kesalahan"))
            }
        }
    }

    fun getPlaceDetail(placeId: String) = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val response = apiService1.getPlaceDetail(PlaceDetailsRequest(placeId))
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
                emit(Result.Error("Terjadi Kesalahan"))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: MapsRepository? = null
        fun getInstance(
            apiService1: ApiService1
        ) =
            instance ?: synchronized(this) {
                instance ?: MapsRepository(apiService1)
            }.also { instance = it }
    }
}
