package com.lokavo.data.remote.retrofit

import com.lokavo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(
                if (com.android.volley.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            )

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }
    }
}