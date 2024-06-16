package com.lokavo.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lokavo.data.local.entity.AnalyzeHistory
import com.lokavo.data.local.entity.ChatBotHistory
import com.lokavo.data.local.entity.ChatBotHistoryDetail

@Database(entities = [AnalyzeHistory::class, ChatBotHistory::class, ChatBotHistoryDetail::class], version = 1, exportSchema = false)
abstract class LokavoDatabase : RoomDatabase() {
    abstract fun analyzeHistoryDao(): AnalyzeHistoryDao
    abstract fun chatBotHistoryDao(): ChatBotHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: LokavoDatabase? = null
        fun getDatabase(context: Context): LokavoDatabase =
            INSTANCE ?: synchronized(LokavoDatabase::class.java) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LokavoDatabase::class.java, "lokavo_database"
                ).build()
            }
    }
}