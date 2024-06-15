package com.lokavo.data.repository

import androidx.lifecycle.LiveData
import com.lokavo.data.local.entity.AnalyzeHistory
import com.lokavo.data.local.room.AnalyzeHistoryDao
import java.util.concurrent.ExecutorService

class AnalyzeHistoryRepository(
    private val analyzeHistoryDao: AnalyzeHistoryDao,
    private val executorService: ExecutorService
) {
    fun insertOrUpdate(userId: String, analyzeHistory: AnalyzeHistory) {
        executorService.execute {
            val existingHistory = analyzeHistory.latitude?.let {
                analyzeHistory.longitude?.let { it1 ->
                    analyzeHistoryDao.findByLatLong(
                        userId,
                        it,
                        it1
                    )
                }
            }
            if (existingHistory != null) {
                existingHistory.date = analyzeHistory.date
                analyzeHistoryDao.update(existingHistory)
            } else {
                analyzeHistoryDao.insert(analyzeHistory)
            }
        }
    }

    fun delete(analyzeHistory: AnalyzeHistory) {
        executorService.execute { analyzeHistoryDao.delete(analyzeHistory) }
    }

    fun deleteAll(userId: String) {
        executorService.execute { analyzeHistoryDao.deleteAll(userId) }
    }

    fun getAll(userId: String): LiveData<List<AnalyzeHistory>> =
        analyzeHistoryDao.getAll(userId)

    companion object {
        @Volatile
        private var instance: AnalyzeHistoryRepository? = null
        fun getInstance(
            analyzeHistoryDao: AnalyzeHistoryDao,
            executorService: ExecutorService
        ): AnalyzeHistoryRepository =
            instance ?: synchronized(this) {
                instance ?: AnalyzeHistoryRepository(analyzeHistoryDao, executorService)
            }.also { instance = it }
    }
}