package com.isyara.app.data.repository

import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.LoginResponse
import com.isyara.app.data.remote.response.LogoutResponse
import com.isyara.app.data.remote.response.RegisterResponse
import com.isyara.app.data.remote.retrofit.ApiService
import com.isyara.app.util.safeApiCall

class AuthRepository private constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return safeApiCall {
            apiService.login(email, password, "user")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Result<RegisterResponse> {
        return safeApiCall {
            apiService.register(name, email, password, passwordConfirmation)
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