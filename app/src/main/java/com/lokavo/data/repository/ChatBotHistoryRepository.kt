package com.lokavo.data.repository

import androidx.lifecycle.LiveData
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.local.room.ChatBotHistoryDao
import java.util.concurrent.ExecutorService

class ChatBotHistoryRepository(
    private val chatBotHistoryDao: ChatBotHistoryDao,
    private val executorService: ExecutorService
) {
    fun insertOrUpdate(userId: String, chatBotHistory: ChatBotHistory) {
        executorService.execute {
            val existingHistory = chatBotHistory.latitude?.let {
                chatBotHistory.longitude?.let { it1 ->
                    chatBotHistoryDao.findByLatLong(
                        userId,
                        it,
                        it1
                    )
                }
            }
            if (existingHistory != null) {
                existingHistory.date = chatBotHistory.date
                chatBotHistoryDao.update(existingHistory)
            } else {
                chatBotHistoryDao.insert(chatBotHistory)
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