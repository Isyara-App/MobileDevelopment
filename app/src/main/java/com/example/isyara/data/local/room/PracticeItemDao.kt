package com.example.isyara.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.isyara.data.local.entity.PracticeItem

@Dao
interface PracticeItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(practiceItem: PracticeItem)

    @Delete
    suspend fun delete(practiceItem: PracticeItem)

    @Query("SELECT * FROM practice_item ORDER BY id DESC")
    suspend fun getAllPracticeItems(): List<PracticeItem>

    @Query("SELECT * FROM practice_item WHERE id = :id LIMIT 1")
    suspend fun getPracticeItemById(id: Int): PracticeItem?
}
