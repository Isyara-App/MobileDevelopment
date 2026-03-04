package com.isyara.app.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.isyara.app.data.local.entity.PracticeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Database(entities = [PracticeItem::class], version = 2, exportSchema = false)
abstract class SpeakDatabase : RoomDatabase() {

    abstract fun practiceItemDao(): PracticeItemDao

    companion object {
        @Volatile
        private var INSTANCE: SpeakDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): SpeakDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SpeakDatabase::class.java, "speak_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
