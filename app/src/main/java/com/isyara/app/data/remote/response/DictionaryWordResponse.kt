package com.isyara.app.data.remote.response

import com.google.gson.annotations.SerializedName

data class DictionaryWordResponse(

    @field:SerializedName("data")
    val data: List<DataItemWord?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataItemWord(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("huruf")
    val huruf: String? = null,

    @field:SerializedName("is_hijaiyah")
    val isHijaiyah: Boolean? = null,

    @field:SerializedName("is_bisindo")
    val isBisindo: Boolean? = null,

    @field:SerializedName("is_knowing")
    val isKnowing: Boolean? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null
)
