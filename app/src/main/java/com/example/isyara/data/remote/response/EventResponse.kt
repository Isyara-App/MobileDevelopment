package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class EventResponse(

    @field:SerializedName("data")
    val data: List<DataEvent?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataEvent(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null
)
