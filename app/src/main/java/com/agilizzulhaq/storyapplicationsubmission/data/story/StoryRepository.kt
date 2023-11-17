package com.agilizzulhaq.storyapplicationsubmission.data.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.agilizzulhaq.storyapplicationsubmission.data.api.ApiService
import com.agilizzulhaq.storyapplicationsubmission.data.preferences.UserPreferences
import com.agilizzulhaq.storyapplicationsubmission.data.responses.AddStoryResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.ListStoryItem
import com.agilizzulhaq.storyapplicationsubmission.data.responses.LoginResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.LoginResult
import com.agilizzulhaq.storyapplicationsubmission.data.responses.SignupResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.StoryResponse
import com.agilizzulhaq.storyapplicationsubmission.data.source.AppDataSource
import com.agilizzulhaq.storyapplicationsubmission.data.source.RemoteDataSource
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val preferences: UserPreferences,
    private val remoteDataSource: RemoteDataSource,
    private val storyRoomDatabase: StoryRoomDatabase
) : AppDataSource {

    override fun getUser(): LiveData<LoginResult> {
        return preferences.getUser().asLiveData()
    }

    override fun login(email: String, password: String): LiveData<LoginResponse> {
        val loginResponses = MutableLiveData<LoginResponse>()

        remoteDataSource.login(object : RemoteDataSource.LoginCallback {
            override fun onLogin(loginResponse: LoginResponse) {
                loginResponses.postValue(loginResponse)
            }
        }, email, password)
        return loginResponses
    }

    override fun signup(name: String, email: String, password: String): LiveData<SignupResponse> {
        val signupResponses = MutableLiveData<SignupResponse>()

        remoteDataSource.signup(object : RemoteDataSource.SignupCallback {
            override fun onSignup(signupResponse: SignupResponse) {
                signupResponses.postValue(signupResponse)
            }
        }, name, email, password)
        return signupResponses
    }

    override fun addStory(token: String, imageFile: File, desc: String, lon: String?, lat: String?): LiveData<AddStoryResponse> {
        val uploadResponseStatus = MutableLiveData<AddStoryResponse>()

        remoteDataSource.addStory(object : RemoteDataSource.AddStoryCallback {
            override fun onAddStory(addStoryResponse: AddStoryResponse) {
                uploadResponseStatus.postValue(addStoryResponse)
            }
        }, token, imageFile, desc, lon, lat)
        return uploadResponseStatus
    }

    override fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoryPagingSource(
                    apiService = apiService,
                    userPreferences = preferences
                )
            }
        ).liveData
    }

    override fun getListMapsStory(token: String): LiveData<StoryResponse> {
        val storyResponses = MutableLiveData<StoryResponse>()
        remoteDataSource.getListMapsStory(object : RemoteDataSource.GetListMapsStoryCallback {
            override fun onMapsStoryLoad(storyResponse: StoryResponse) {
                storyResponses.postValue(storyResponse)
            }

        }, token)
        return storyResponses
    }

    suspend fun saveUser(userName: String, userId: String, userToken: String) {
        preferences.saveUser(userName, userId, userToken)
    }

    suspend fun logout() {
        preferences.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreferences,
            remoteDataSource: RemoteDataSource,
            storyRoomDatabase: StoryRoomDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref, remoteDataSource, storyRoomDatabase)
            }.also { instance = it }
    }
}