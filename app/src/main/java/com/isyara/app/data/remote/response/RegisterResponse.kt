package com.isyara.app.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("user")
    val user: UserData? = null
)
