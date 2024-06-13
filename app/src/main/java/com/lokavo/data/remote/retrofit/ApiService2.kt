package com.lokavo.data.remote.retrofit

import com.lokavo.data.remote.request.ChatBotRequest
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.data.remote.response.ChatBotResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService2 {
    @POST("/chatbot")
    suspend fun postChatBot(
        @Body request: ChatBotRequest
    ): ChatBotResponse

    @GET("chatbot/{uuid}/{parameter}")
    suspend fun getChatBotMessage(
        @Path("uuid") uuid: String,
        @Path("parameter") parameter: Int
    ): ChatBotMessageResponse
}