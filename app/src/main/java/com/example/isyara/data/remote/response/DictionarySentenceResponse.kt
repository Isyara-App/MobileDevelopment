package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class DictionarySentenceResponse(

    @field:SerializedName("data")
    val data: List<DataItemSentence?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataItemSentence(

    @field:SerializedName("kata")
    val kata: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
