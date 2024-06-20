package com.lokavo.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ArticleResponse
import com.lokavo.data.repository.ArticleRepository

class ArticleViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val _articles = MutableLiveData<Result<ArticleResponse>>()
    val articles: LiveData<Result<ArticleResponse>> get() = _articles

    var hasLoaded = false

    fun loadArticles() {
        if (!hasLoaded) {
            repository.getArticles().observeForever { result ->
                _articles.value = result
            }
        }
    }
}
