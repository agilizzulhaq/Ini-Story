package com.agilizzulhaq.storyapplicationsubmission.di

import android.content.Context
import com.agilizzulhaq.storyapplicationsubmission.data.api.ApiConfig
import com.agilizzulhaq.storyapplicationsubmission.data.preferences.UserPreferences
import com.agilizzulhaq.storyapplicationsubmission.data.source.RemoteDataSource
import com.agilizzulhaq.storyapplicationsubmission.data.story.StoryRepository
import com.agilizzulhaq.storyapplicationsubmission.data.story.StoryRoomDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferenceDatastore = UserPreferences.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val storyRoomDatabase = StoryRoomDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, userPreferenceDatastore, remoteDataSource, storyRoomDatabase)
    }
}