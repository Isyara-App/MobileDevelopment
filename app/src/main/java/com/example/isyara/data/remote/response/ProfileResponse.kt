package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

    @field:SerializedName("data")
    val data: DataProfile? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataProfile(

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null
)
