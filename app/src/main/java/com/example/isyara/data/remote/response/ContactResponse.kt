package com.example.isyara.data.remote.response

import com.google.gson.annotations.SerializedName

data class ContactResponse(

    @field:SerializedName("data")
    val data: DataContact? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataContact(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)
