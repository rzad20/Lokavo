package com.lokavo.data.repository

import androidx.lifecycle.LiveData
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.local.entity.ChatBotHistoryDetail
import com.lokavo.data.local.room.ChatBotHistoryDao
import java.util.concurrent.ExecutorService

class ChatBotHistoryRepository(
    private val chatBotHistoryDao: ChatBotHistoryDao,
    private val executorService: ExecutorService
) {
    fun insertOrUpdate(
        userId: String,
        chatBotHistory: ChatBotHistory,
        details: List<ChatBotHistoryDetail>
    ) {
        executorService.execute {
            val existingHistory = chatBotHistoryDao.findByLatLong(
                userId,
                chatBotHistory.latitude!!,
                chatBotHistory.longitude!!
            )
            if (existingHistory != null) {
                existingHistory.chatBotHistory.date = chatBotHistory.date
                chatBotHistoryDao.updateChatBotHistoryWithDetails(existingHistory.chatBotHistory, details)
            } else {
                chatBotHistoryDao.insertChatBotHistoryWithDetails(chatBotHistory, details)
            }
        }
    }

    fun delete(chatBotHistory: ChatBotHistory) {
        executorService.execute { chatBotHistoryDao.delete(chatBotHistory) }
    }

    fun deleteAll(userId: String) {
        executorService.execute { chatBotHistoryDao.deleteAll(userId) }
    }

    fun getAll(userId: String): LiveData<List<ChatBotHistory>> =
        chatBotHistoryDao.getAll(userId)

    fun findByLatLong(userId: String, latitude: Double, longitude: Double)
    = chatBotHistoryDao.findByLatLong(userId, latitude, longitude)

    companion object {
        @Volatile
        private var instance: ChatBotHistoryRepository? = null
        fun getInstance(
            chatBotHistoryDao: ChatBotHistoryDao,
            executorService: ExecutorService
        ): ChatBotHistoryRepository =
            instance ?: synchronized(this) {
                instance ?: ChatBotHistoryRepository(chatBotHistoryDao, executorService)
            }.also { instance = it }
    }
}
