package com.lokavo.data.repository

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lokavo.data.Result
import com.lokavo.data.remote.request.ChatBotRequest
import com.lokavo.data.remote.response.PlaceDetailsResponse
import com.lokavo.data.remote.retrofit.ApiService2
import retrofit2.HttpException
import java.net.SocketTimeoutException

class ChatBotRepository(private var apiService2: ApiService2) {
    fun postChatBot(latitude: Double, longitude: Double, uuid: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService2.postChatBot(ChatBotRequest(latitude, longitude, uuid))
            if (response.status == "processing" && !response.uuid.isNullOrEmpty()) {
                emit(Result.Success(response))
            } else {
                emit(response.message?.let { Result.Error(it) })
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, PlaceDetailsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request Timeout"))
        } catch (e: Exception) {
            emit(Result.Error("Terjadi Kesalahan"))
        }
    }

    fun getChatBotMessage(uuid: String, parameter: Int) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService2.getChatBotMessage(uuid, parameter)
            if (!response.answer.isNullOrEmpty() && !response.question.isNullOrEmpty()) {
                emit(Result.Success(response))
            } else {
                emit(response.error?.let { Result.Error(it) })
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, PlaceDetailsResponse::class.java)
            emit(errorResponse.message?.let { Result.Error(it) })
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request Timeout"))
        } catch (e: Exception) {
            emit(Result.Error("Terjadi Kesalahan"))
        }
    }

    companion object {
        @Volatile
        private var instance: ChatBotRepository? = null
        fun getInstance(
            apiService2: ApiService2
        ) =
            instance ?: synchronized(this) {
                instance ?: ChatBotRepository(apiService2)
            }.also { instance = it }
    }
}