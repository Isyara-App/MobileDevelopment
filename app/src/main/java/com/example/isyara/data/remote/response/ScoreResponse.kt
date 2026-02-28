package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class ScoreListResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: List<UserProgressData>? = null
)

data class ScoreDetailResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: UserProgressData? = null
)

data class UserProgressData(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("level_id")
    val levelId: Int? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("score")
    val score: Int? = null,

    @field:SerializedName("correct_answers")
    val correctAnswers: Int? = null,

    @field:SerializedName("total_questions")
    val totalQuestions: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null
)
