package com.agilizzulhaq.storyapplicationsubmission.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.agilizzulhaq.storyapplicationsubmission.data.responses.AddStoryResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.ListStoryItem
import com.agilizzulhaq.storyapplicationsubmission.data.responses.LoginResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.LoginResult
import com.agilizzulhaq.storyapplicationsubmission.data.responses.SignupResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.StoryResponse
import java.io.File

interface AppDataSource {
    fun getUser(): LiveData<LoginResult>
    fun login(email: String, password: String): LiveData<LoginResponse>
    fun signup(name: String, email: String, password: String): LiveData<SignupResponse>
    fun addStory(token: String, imageFile: File, desc: String, lon: String?, lat: String?): LiveData<AddStoryResponse>
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>>
    fun getListMapsStory(token: String): LiveData<StoryResponse>
}