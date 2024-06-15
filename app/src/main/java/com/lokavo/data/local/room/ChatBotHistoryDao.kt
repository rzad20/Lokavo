package com.lokavo.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lokavo.data.local.entity.ChatBotHistory

@Dao
interface ChatBotHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(chatBotHistory: ChatBotHistory)

    @Query("SELECT * FROM chat_bot_history WHERE user_id = :userId ORDER BY date DESC")
    fun getAll(userId: String): LiveData<List<ChatBotHistory>>

    @Query("SELECT * FROM chat_bot_history WHERE user_id = :userId AND latitude = :latitude AND longitude = :longitude LIMIT 1")
    fun findByLatLong(userId: String, latitude: Double, longitude: Double): ChatBotHistory?

    @Update
    fun update(chatBotHistory: ChatBotHistory)

    @Delete
    fun delete(chatBotHistory: ChatBotHistory)

    @Query("DELETE FROM chat_bot_history WHERE user_id = :userId")
    fun deleteAll(userId: String)
}