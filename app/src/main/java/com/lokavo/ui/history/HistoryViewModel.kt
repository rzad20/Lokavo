package com.lokavo.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.repository.HistoryRepository
import com.lokavo.data.local.entity.History

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    fun delete(history: History) {
        historyRepository.delete(history)
    }

    fun insertOrUpdate(history: History) {
        historyRepository.insertOrUpdate(history)
    }

    fun getAll(): LiveData<List<History>> {
        return historyRepository.getAll()
    }

    fun deleteAll() {
        historyRepository.deleteAll()
    }
}
