package com.agilizzulhaq.storyapplicationsubmission.data.responses

import com.google.gson.annotations.SerializedName

data class SignupResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
