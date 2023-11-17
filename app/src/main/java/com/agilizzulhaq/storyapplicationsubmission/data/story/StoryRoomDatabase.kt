package com.agilizzulhaq.storyapplicationsubmission.data.story

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.agilizzulhaq.storyapplicationsubmission.data.remote.RemoteKeys
import com.agilizzulhaq.storyapplicationsubmission.data.remote.RemoteKeysDao
import com.agilizzulhaq.storyapplicationsubmission.data.responses.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 3,
    exportSchema = false
)
abstract class StoryRoomDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    companion object {
        @Volatile
        private var INSTANCE: StoryRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryRoomDatabase::class.java,
                    "story_db").build()
            }
        }
    }
}