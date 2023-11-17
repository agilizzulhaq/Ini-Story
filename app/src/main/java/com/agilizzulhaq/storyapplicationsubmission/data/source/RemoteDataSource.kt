package com.agilizzulhaq.storyapplicationsubmission.data.source

import androidx.lifecycle.MutableLiveData
import com.agilizzulhaq.storyapplicationsubmission.data.api.ApiConfig
import com.agilizzulhaq.storyapplicationsubmission.data.responses.AddStoryResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.LoginResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.SignupResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.StoryResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteDataSource {
    val error = MutableLiveData("")
    var response = ""

    fun login(callback: LoginCallback, email: String, password: String) {
        callback.onLogin(
            LoginResponse(
                null,
                true,
                ""
            )
        )

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                if(response.isSuccessful){
                    response.body()?.let { callback.onLogin(it) }
                }else {
                    when (response.code()) {
                        200 -> this@RemoteDataSource.response = "200"
                        400 -> this@RemoteDataSource.response = "400"
                        401 -> this@RemoteDataSource.response = "401"
                        else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                    }
                    callback.onLogin(
                        LoginResponse(
                            null,
                            true,
                            this@RemoteDataSource.response
                        )
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onLogin(
                    LoginResponse(
                        null,
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun signup(callback: SignupCallback, name: String, email: String, password: String){
        val signupInfo = SignupResponse(
            true,
            ""
        )
        callback.onSignup(
            signupInfo
        )
        val client = ApiConfig.getApiService().signup(name, email, password)
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if(response.isSuccessful){
                    response.body()?.let { callback.onSignup(it) }
                    this@RemoteDataSource.response = "201"
                    callback.onSignup(
                        SignupResponse(
                            true,
                            this@RemoteDataSource.response
                        )
                    )
                }else {
                    this@RemoteDataSource.response = "400"
                    callback.onSignup(
                        SignupResponse(
                            true,
                            this@RemoteDataSource.response
                        )
                    )
                }
            }
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                callback.onSignup(
                    SignupResponse(
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun addStory(callback: AddStoryCallback, token: String, imageFile: File, desc: String, lon: String? = null, lat: String? = null){
        callback.onAddStory(
            addStoryResponse = AddStoryResponse(
                true,
                ""
            )
        )

        val description = desc.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().addStory(token = "Bearer $token", imageMultipart, description, latitude!!, longitude!!)

        client.enqueue(object : Callback<AddStoryResponse>{
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        callback.onAddStory(responseBody)
                    }else{
                        callback.onAddStory(
                            addStoryResponse = AddStoryResponse(
                                true,
                                "Gagal upload file"
                            )
                        )
                    }
                }
                else{
                    callback.onAddStory(
                        addStoryResponse = AddStoryResponse(
                            true,
                            "Gagal upload file"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                callback.onAddStory(
                    addStoryResponse = AddStoryResponse(
                        true,
                        "Gagal upload file"
                    )
                )
            }
        })
    }

    fun getListMapsStory(callback: GetListMapsStoryCallback, token: String){
        val client = ApiConfig.getApiService().getListMapsStory(token = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { callback.onMapsStoryLoad(it) }
                }else{
                    val storyResponse = StoryResponse(
                        emptyList(),
                        true,
                        "Load Failed!"
                    )
                    callback.onMapsStoryLoad(storyResponse)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                val storyResponse = StoryResponse(
                    emptyList(),
                    true,
                    t.message.toString()
                )
                callback.onMapsStoryLoad(storyResponse)
            }
        })
    }


    interface LoginCallback{
        fun onLogin(loginResponse: LoginResponse)
    }

    interface SignupCallback{
        fun onSignup(signupResponse: SignupResponse)
    }

    interface GetListMapsStoryCallback{
        fun onMapsStoryLoad(storyResponse: StoryResponse)
    }

    interface AddStoryCallback{
        fun onAddStory(addStoryResponse: AddStoryResponse)
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }
}