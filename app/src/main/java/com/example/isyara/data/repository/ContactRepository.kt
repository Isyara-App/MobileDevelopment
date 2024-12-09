package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.ContactResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.safeApiCall

class ContactRepository private constructor(private val apiService: ApiService) {
    suspend fun sendContact(
        token: String,
        name: String,
        email: String,
        message: String
    ): Result<ContactResponse> {
        return safeApiCall {
            apiService.contact("Bearer $token", name, email, message)
        }
    }

    companion object {
        @Volatile
        private var instance: ContactRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): ContactRepository =
            instance ?: synchronized(this) {
                instance ?: ContactRepository(apiService)
            }.also { instance = it }
    }
}