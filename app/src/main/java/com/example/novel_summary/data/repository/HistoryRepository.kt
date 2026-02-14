package com.example.novel_summary.data.repository


import com.example.novel_summary.data.dao.HistoryDao
import com.example.novel_summary.data.model.History
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistory: Flow<List<History>> = historyDao.getAllHistory()

    suspend fun insertHistory(history: History) {
        historyDao.insertHistory(history)
    }

    suspend fun deleteHistory(history: History) {
        historyDao.deleteHistory(history)
    }

    suspend fun deleteAllHistory() {
        historyDao.deleteAllHistory()
    }

    suspend fun getHistoryById(id: Long): History? {
        return historyDao.getHistoryById(id)
    }

    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteHistoryById(id)
    }
}