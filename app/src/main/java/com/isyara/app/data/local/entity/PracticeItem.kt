package com.isyara.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_item")
data class PracticeItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "image_uri")
    val imageUri: String,

    @ColumnInfo(name = "target_text")
    val targetText: String
)
