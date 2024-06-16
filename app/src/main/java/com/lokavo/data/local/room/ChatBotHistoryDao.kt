package com.lokavo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.local.entity.ChatBotHistoryDetail
import com.lokavo.data.local.entity.ChatBotHistoryWithDetails

@Dao
interface ChatBotHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(chatBotHistory: ChatBotHistory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDetail(chatBotHistoryDetail: ChatBotHistoryDetail)

    @Transaction
    @Query("SELECT * FROM chat_bot_history WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): LiveData<List<ChatBotHistory>>

    @Query("SELECT * FROM chat_bot_history WHERE user_id = :userId AND latitude = :latitude AND longitude = :longitude LIMIT 1")
    fun findByLatLong(
        userId: String,
        latitude: Double,
        longitude: Double
    ): ChatBotHistoryWithDetails?

    @Update
    fun update(chatBotHistory: ChatBotHistory)

    @Transaction
    fun insertChatBotHistoryWithDetails(
        chatBotHistory: ChatBotHistory,
        details: List<ChatBotHistoryDetail>
    ) {
        val historyId = insert(chatBotHistory)
        details.forEach { detail ->
            detail.historyId = historyId
            insertDetail(detail)
        }
    }

    @Transaction
    fun updateChatBotHistoryWithDetails(
        chatBotHistory: ChatBotHistory,
        details: List<ChatBotHistoryDetail>
    ) {
        update(chatBotHistory)
        chatBotHistory.id?.let { deleteDetailsByHistoryId(it) }
        details.forEach { detail ->
            detail.historyId = chatBotHistory.id
            insertDetail(detail)
        }
    }

    @Delete
    fun delete(chatBotHistory: ChatBotHistory)

    @Query("DELETE FROM chat_bot_history WHERE user_id = :userId")
    fun deleteAll(userId: String)

    @Query("DELETE FROM chat_bot_history_detail WHERE historyId = :historyId")
    fun deleteDetailsByHistoryId(historyId: Long)
}