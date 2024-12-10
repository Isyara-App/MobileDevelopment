package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class CheckAnswerResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("isCorrect")
	val isCorrect: Boolean? = null
)
