package com.lokavo.ui.detailAnalysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ChatBotResponse
import com.lokavo.data.repository.ChatBotRepository

class DetailAnalysisViewModel(private val repository: ChatBotRepository) : ViewModel() {
    fun postChatBot(
        latitude: Double,
        longitude: Double,
        uuid: String
    ): LiveData<Result<ChatBotResponse>?> {
        return repository.postChatBot(latitude, longitude, uuid)
    }
}