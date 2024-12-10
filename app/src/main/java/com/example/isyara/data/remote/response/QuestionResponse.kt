package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class QuestionResponse(

    @field:SerializedName("data")
    val data: Question? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class Question(

    @field:SerializedName("correct_option")
    val correctOption: String? = null,

    @field:SerializedName("question")
    val question: String? = null,

    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("options")
    val options: List<String?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
