package com.lokavo.ui.analyzeHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lokavo.data.repository.AnalyzeHistoryRepository
import com.lokavo.data.local.entity.AnalyzeHistory

class AnalyzeHistoryViewModel(
    private val analyzeHistoryRepository: AnalyzeHistoryRepository
) : ViewModel() {
    fun delete(analyzeHistory: AnalyzeHistory) {
        analyzeHistoryRepository.delete(analyzeHistory)
    }

    fun insertOrUpdate(userId: String, analyzeHistory: AnalyzeHistory) {
        analyzeHistoryRepository.insertOrUpdate(userId, analyzeHistory)
    }

    fun getAll(userId: String): LiveData<List<AnalyzeHistory>> {
        return analyzeHistoryRepository.getAll(userId)
    }

    fun deleteAll(userId: String) {
        analyzeHistoryRepository.deleteAll(userId)
    }
}