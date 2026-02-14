package com.example.novel_summary.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.novel_summary.data.model.History
import com.example.novel_summary.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository: HistoryRepository

    init {
        val database = com.example.novel_summary.App.database
        historyRepository = HistoryRepository(database.historyDao())
    }

    fun getAllHistory() = historyRepository.allHistory

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            historyRepository.deleteHistory(history)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            historyRepository.deleteAllHistory()
        }
    }

    // FIXED: Add insertHistory method
    fun insertHistory(history: History) {
        viewModelScope.launch {
            historyRepository.insertHistory(history)
        }
    }
}