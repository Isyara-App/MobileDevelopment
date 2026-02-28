package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class LearningStatusResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: LearningStatusData? = null
)

data class LearningStatusData(

    @field:SerializedName("kamus_huruf_id")
    val kamusHurufId: String? = null,

    @field:SerializedName("kamus_kata_id")
    val kamusKataId: String? = null,

    @field:SerializedName("is_knowing")
    val isKnowing: Boolean? = null
)
