package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.LoginResponse
import com.example.isyara.data.remote.response.LogoutResponse
import com.example.isyara.data.remote.response.RegisterResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.safeApiCall

class AuthRepository private constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return safeApiCall {
            apiService.login(email, password)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return safeApiCall {
            apiService.register(name, email, password)
        }
    }

    suspend fun logout(token: String): Result<LogoutResponse> {
        return safeApiCall {
            apiService.logout("Bearer $token")
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}