package com.example.novel_summary.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.novel_summary.data.model.Bookmark
import com.example.novel_summary.data.repository.BookmarkRepository
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val bookmarkRepository: BookmarkRepository

    init {
        val database = com.example.novel_summary.App.database
        bookmarkRepository = BookmarkRepository(database.bookmarkDao())
    }

    fun getAllBookmarks() = bookmarkRepository.allBookmarks

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmark)
        }
    }

    fun deleteAllBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.deleteAllBookmarks()
        }
    }

    // FIXED: Add insertBookmark method
    fun insertBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.insertBookmark(bookmark)
        }
    }

    suspend fun getBookmarkByUrl(url: String): Bookmark? {
        return bookmarkRepository.getBookmarkByUrl(url)
    }
}