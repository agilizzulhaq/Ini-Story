package com.agilizzulhaq.storyapplicationsubmission.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agilizzulhaq.storyapplicationsubmission.data.story.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun getUser() = repository.getUser()

    fun saveUser(userName: String, userId: String, userToken: String) {
        viewModelScope.launch {
            repository.saveUser(userName, userId, userToken)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}