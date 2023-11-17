package com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.agilizzulhaq.storyapplicationsubmission.data.story.StoryRepository

class SignupViewModel(private val repository: StoryRepository) : ViewModel() {

    fun signup(name: String, email: String, password: String) = repository.signup(name, email, password)
}