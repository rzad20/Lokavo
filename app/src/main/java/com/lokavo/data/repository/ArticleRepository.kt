package com.lokavo.data.repository

import androidx.lifecycle.liveData
import com.lokavo.data.Result
import com.lokavo.data.remote.retrofit.ApiService1

class ArticleRepository private constructor(private var apiService1: ApiService1) {
    fun getArticles() = liveData {
        emit(Result.Loading)
        try {
            val response = apiService1.getArticles()
            if (response.list.isNullOrEmpty()) {
                emit(Result.Empty)
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error("Terjadi Kesalahan"))
        }
    }

    companion object {
        @Volatile
        private var instance: ArticleRepository? = null
        fun getInstance(
            apiService1: ApiService1
        ) =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(apiService1)
            }.also { instance = it }
    }
}