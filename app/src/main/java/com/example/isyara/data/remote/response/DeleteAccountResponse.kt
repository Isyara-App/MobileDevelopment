package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeleteAccountResponse(

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)
