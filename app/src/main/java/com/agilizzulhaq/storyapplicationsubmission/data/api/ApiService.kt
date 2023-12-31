package com.agilizzulhaq.storyapplicationsubmission.data.api

import com.agilizzulhaq.storyapplicationsubmission.data.responses.AddStoryResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.LoginResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.SignupResponse
import com.agilizzulhaq.storyapplicationsubmission.data.responses.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun signup(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<SignupResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String?,
        @QueryMap queries: Map<String, Int>,
    ): StoryResponse

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddStoryResponse>

    @GET("stories?location=1")
    fun getListMapsStory(
        @Header("Authorization") token: String?
    ): Call<StoryResponse>
}