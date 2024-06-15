package com.lokavo.ui.chatBotHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.repository.ChatBotHistoryRepository

class ChatBotHistoryViewModel(private val chatBotHistoryRepository: ChatBotHistoryRepository) : ViewModel() {
    fun delete(chatBotHistory: ChatBotHistory) {
        chatBotHistoryRepository.delete(chatBotHistory)
    }

    fun insertOrUpdate(userId: String, chatBotHistory: ChatBotHistory) {
        chatBotHistoryRepository.insertOrUpdate(userId, chatBotHistory)
    }

    fun getAll(userId: String): LiveData<List<ChatBotHistory>> {
        return chatBotHistoryRepository.getAll(userId)
    }

    fun deleteAll(userId: String) {
        chatBotHistoryRepository.deleteAll(userId)
    }
}