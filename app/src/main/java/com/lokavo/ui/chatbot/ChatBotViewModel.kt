package com.lokavo.ui.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.Result
import com.lokavo.data.remote.response.ChatBotMessageResponse
import com.lokavo.data.repository.ChatBotRepository

class ChatBotViewModel(private val repository: ChatBotRepository) : ViewModel() {
    fun getChatBotMessage(
        uuid: String,
        parameter: Int
    ): LiveData<Result<ChatBotMessageResponse>?> {
        return repository.getChatBotMessage(uuid, parameter)
    }
}