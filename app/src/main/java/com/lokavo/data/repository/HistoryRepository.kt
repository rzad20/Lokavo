package com.lokavo.data.repository

import androidx.lifecycle.LiveData
import com.lokavo.data.local.entity.History
import com.lokavo.data.local.room.HistoryDao
import java.util.concurrent.ExecutorService

class HistoryRepository(
    private val historyDao: HistoryDao,
    private val executorService: ExecutorService
) {
    fun insertOrUpdate(history: History) {
        executorService.execute {
            val existingHistory = history.latitude?.let {
                history.longitude?.let { it1 ->
                    historyDao.findByLatLong(
                        it,
                        it1
                    )
                }
            }
            if (existingHistory != null) {
                existingHistory.date = history.date
                historyDao.update(existingHistory)
            } else {
                historyDao.insert(history)
            }
        }
    }

    fun delete(history: History) {
        executorService.execute { historyDao.delete(history) }
    }

    fun deleteAll() {
        executorService.execute { historyDao.deleteAll() }
    }

    fun getAll(): LiveData<List<History>> =
        historyDao.getAll()

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(
            historyDao: HistoryDao,
            executorService: ExecutorService
        ): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(historyDao, executorService)
            }.also { instance = it }
    }
}