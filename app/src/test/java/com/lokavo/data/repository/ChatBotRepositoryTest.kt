package com.lokavo.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lokavo.data.Result
import com.lokavo.data.remote.request.ChatBotRequest
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.data.remote.response.ChatBotResponse
import com.lokavo.data.remote.retrofit.ApiService2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.HttpException
import retrofit2.Response

class ChatBotRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var apiService2: ApiService2
    private lateinit var chatBotRepository: ChatBotRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        apiService2 = mock(ApiService2::class.java)
        chatBotRepository = ChatBotRepository.getInstance(apiService2)
    }

    @Test
    fun `postChatBot returns success`() = runBlocking {
        val chatBotRequest = ChatBotRequest(1.0, 2.0, "uuid")
        val response = ChatBotResponse(null,"uuid", "processing")

        `when`(apiService2.postChatBot(chatBotRequest)).thenReturn(response)

        val observer = mock(Observer::class.java) as Observer<Result<ChatBotResponse>?>

        val liveData = chatBotRepository.postChatBot(1.0, 2.0, "uuid")
        liveData.observeForever(observer)

        verify(observer).onChanged(Result.Loading)
        verify(observer).onChanged(Result.Success(response))

        liveData.removeObserver(observer)
    }

    @Test
    fun `getChatBotMessage returns success`() = runBlocking {
        val response = ChatBotMessageResponse("answer", "question", null)

        `when`(apiService2.getChatBotMessage(anyString(), anyInt())).thenReturn(response)

        val observer = mock(Observer::class.java) as Observer<Result<ChatBotMessageResponse>?>

        val liveData = chatBotRepository.getChatBotMessage("uuid", 1)
        liveData.observeForever(observer)

        verify(observer).onChanged(Result.Loading)
        verify(observer).onChanged(Result.Success(response))

        liveData.removeObserver(observer)
    }

    @Test
    fun `getChatBotMessage returns error`() = runBlocking {
        val errorMessage = "Error message"
        val errorBody = "{\"message\":\"$errorMessage\"}".toResponseBody(null)
        val response = Response.error<ChatBotMessageResponse>(400, errorBody)

        `when`(apiService2.getChatBotMessage(anyString(), anyInt())).thenThrow(HttpException(response))

        val observer = mock(Observer::class.java) as Observer<Result<ChatBotMessageResponse>?>

        val liveData = chatBotRepository.getChatBotMessage("uuid", 1)
        liveData.observeForever(observer)

        verify(observer).onChanged(Result.Loading)
        verify(observer).onChanged(Result.Error(errorMessage))

        liveData.removeObserver(observer)
    }
}
