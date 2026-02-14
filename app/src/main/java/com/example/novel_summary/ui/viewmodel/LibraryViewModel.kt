package com.example.novel_summary.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.novel_summary.data.model.Novel
import com.example.novel_summary.data.model.Volume
import com.example.novel_summary.data.model.Chapter
import com.example.novel_summary.data.repository.SummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val summaryRepository: SummaryRepository

    init {
        val database = com.example.novel_summary.App.database
        summaryRepository = SummaryRepository(
            database.novelDao(),
            database.volumeDao(),
            database.chapterDao()
        )
    }

    fun getAllNovels() = summaryRepository.getAllNovels()

    // FIXED: Keep these as regular functions for UI operations
    fun insertNovel(novel: Novel) {
        viewModelScope.launch {
            summaryRepository.insertNovel(novel)
        }
    }

    fun updateNovel(novel: Novel) {
        viewModelScope.launch {
            summaryRepository.updateNovel(novel)
        }
    }

    fun deleteNovel(novel: Novel) {
        viewModelScope.launch {
            summaryRepository.deleteNovel(novel)
        }
    }

    suspend fun getNovelByName(name: String): Novel? {
        return summaryRepository.getNovelByName(name)
    }

    // Volume operations
    fun getVolumesByNovelId(novelId: Long): Flow<List<Volume>> {
        return summaryRepository.getVolumesByNovelId(novelId)
    }

    fun insertVolume(volume: Volume) {
        viewModelScope.launch {
            summaryRepository.insertVolume(volume)
        }
    }

    suspend fun getVolumeByName(novelId: Long, volumeName: String): Volume? {
        return summaryRepository.getVolumeByName(novelId, volumeName)
    }

    fun updateVolume(volume: Volume) {
        viewModelScope.launch {
            summaryRepository.updateVolume(volume)
        }
    }

    fun deleteVolume(volume: Volume) {
        viewModelScope.launch {
            summaryRepository.deleteVolume(volume)
        }
    }

    // Chapter operations
    fun getChaptersByVolumeId(volumeId: Long): Flow<List<Chapter>> {
        return summaryRepository.getChaptersByVolumeId(volumeId)
    }

    fun insertChapter(chapter: Chapter) {
        viewModelScope.launch {
            summaryRepository.insertChapter(chapter)
        }
    }

    suspend fun getChapterByName(volumeId: Long, chapterName: String): Chapter? {
        return summaryRepository.getChapterByName(volumeId, chapterName)
    }

    fun updateChapter(chapter: Chapter) {
        viewModelScope.launch {
            summaryRepository.updateChapter(chapter)
        }
    }

    fun deleteChapter(chapter: Chapter) {
        viewModelScope.launch {
            summaryRepository.deleteChapter(chapter)
        }
    }
}