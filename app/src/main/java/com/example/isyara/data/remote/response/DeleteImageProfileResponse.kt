package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeleteImageProfileResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
