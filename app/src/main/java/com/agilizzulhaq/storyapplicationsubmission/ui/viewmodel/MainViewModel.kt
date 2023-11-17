package com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agilizzulhaq.storyapplicationsubmission.data.story.StoryRepository
import java.io.File

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    val cordLat = MutableLiveData(0.0)
    val cordLon = MutableLiveData(0.0)

    fun getStory(token: String) =  repository.getStory(token)
    fun addStory(token: String, imageFile: File, desc: String, lon: String?, lat: String?) = repository.addStory(token, imageFile, desc, lon, lat)
    fun getListMapsStory(token: String) = repository.getListMapsStory(token)
}