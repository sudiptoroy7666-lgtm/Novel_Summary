package com.example.novel_summary.data.repository

import com.example.novel_summary.data.dao.ChapterDao
import com.example.novel_summary.data.dao.NovelDao
import com.example.novel_summary.data.dao.VolumeDao
import com.example.novel_summary.data.model.Chapter
import com.example.novel_summary.data.model.Novel
import com.example.novel_summary.data.model.Volume
import kotlinx.coroutines.flow.Flow

class SummaryRepository(
    private val novelDao: NovelDao,
    private val volumeDao: VolumeDao,
    private val chapterDao: ChapterDao
) {

    // Novel operations
    fun getAllNovels(): Flow<List<Novel>> = novelDao.getAllNovels()

    // FIXED: Make insert methods suspend
    suspend fun insertNovel(novel: Novel): Long = novelDao.insertNovel(novel)

    suspend fun getNovelByName(name: String): Novel? = novelDao.getNovelByName(name)

    suspend fun updateNovel(novel: Novel) = novelDao.updateNovel(novel)

    suspend fun deleteNovel(novel: Novel) = novelDao.deleteNovel(novel)

    // Volume operations
    fun getVolumesByNovelId(novelId: Long): Flow<List<Volume>> = volumeDao.getVolumesByNovelId(novelId)

    suspend fun insertVolume(volume: Volume): Long = volumeDao.insertVolume(volume)

    suspend fun getVolumeByName(novelId: Long, volumeName: String): Volume? =
        volumeDao.getVolumeByName(novelId, volumeName)

    suspend fun updateVolume(volume: Volume) = volumeDao.updateVolume(volume)

    suspend fun deleteVolume(volume: Volume) = volumeDao.deleteVolume(volume)

    // Chapter operations
    fun getChaptersByVolumeId(volumeId: Long): Flow<List<Chapter>> = chapterDao.getChaptersByVolumeId(volumeId)

    suspend fun insertChapter(chapter: Chapter): Long = chapterDao.insertChapter(chapter)

    suspend fun getChapterByName(volumeId: Long, chapterName: String): Chapter? =
        chapterDao.getChapterByName(volumeId, chapterName)

    suspend fun updateChapter(chapter: Chapter) = chapterDao.updateChapter(chapter)

    suspend fun deleteChapter(chapter: Chapter) = chapterDao.deleteChapter(chapter)

    suspend fun getChapterById(id: Long): Chapter? = chapterDao.getChapterById(id)
}