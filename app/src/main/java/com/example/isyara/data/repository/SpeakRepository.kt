package com.example.isyara.data.repository

import com.example.isyara.data.local.entity.PracticeItem
import com.example.isyara.data.local.room.PracticeItemDao

class SpeakRepository private constructor(
    private val practiceItemDao: PracticeItemDao
) {
    suspend fun insertPracticeItem(practiceItem: PracticeItem) {
        practiceItemDao.insert(practiceItem)
    }

    suspend fun deletePracticeItem(practiceItem: PracticeItem) {
        practiceItemDao.delete(practiceItem)
    }

    suspend fun getAllPracticeItems(): List<PracticeItem> {
        return practiceItemDao.getAllPracticeItems()
    }

    suspend fun getPracticeItemById(id: Int): PracticeItem? {
        return practiceItemDao.getPracticeItemById(id)
    }

    companion object {
        @Volatile
        private var instance: SpeakRepository? = null
        fun getInstance(
            practiceItemDao: PracticeItemDao
        ): SpeakRepository =
            instance ?: synchronized(this) {
                instance ?: SpeakRepository(practiceItemDao)
            }.also { instance = it }
    }
}
