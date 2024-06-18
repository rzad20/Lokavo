package com.lokavo.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ArticleResponse
import com.lokavo.data.remote.response.ListItem
import com.lokavo.data.remote.retrofit.ApiService1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class ArticleRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var apiService1: ApiService1
    private lateinit var articleRepository: ArticleRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        apiService1 = mock(ApiService1::class.java)
        articleRepository = ArticleRepository.getInstance(apiService1)
    }

    @Test
    fun `getArticles returns success`() = runBlocking {
        val listItems = listOf(
            ListItem("Gambar1", "Ringkasan1", "Headline1", "Link1"),
            ListItem("Gambar2", "Ringkasan2", "Headline2", "Link2")
        )
        val response = ArticleResponse(listItems, 200)

        `when`(apiService1.getArticles()).thenReturn(response)

        val observer = mock(Observer::class.java) as Observer<Result<ArticleResponse>>

        val liveData = articleRepository.getArticles()
        liveData.observeForever(observer)

        verify(observer).onChanged(Result.Loading)
        verify(observer).onChanged(Result.Success(response))

        liveData.removeObserver(observer)
    }

    @Test
    fun `getArticles returns empty`() = runBlocking {
        val response = ArticleResponse(emptyList(), 200)

        `when`(apiService1.getArticles()).thenReturn(response)

        val observer = mock(Observer::class.java) as Observer<Result<ArticleResponse>>

        val liveData = articleRepository.getArticles()
        liveData.observeForever(observer)

        verify(observer).onChanged(Result.Loading)
        verify(observer).onChanged(Result.Empty)

        liveData.removeObserver(observer)
    }

    @Test
    fun `getArticles returns error`() = runBlocking {
        `when`(apiService1.getArticles()).thenThrow(RuntimeException())

        val observer = mock(Observer::class.java) as Observer<Result<ArticleResponse>>

        val liveData = articleRepository.getArticles()
        liveData.observeForever(observer)

        verify(observer).onChanged(Result.Loading)
        verify(observer).onChanged(Result.Error("Terjadi Kesalahan"))

        liveData.removeObserver(observer)
    }
}
