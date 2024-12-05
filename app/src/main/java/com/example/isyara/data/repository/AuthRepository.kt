package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.LoginResponse
import com.example.isyara.data.remote.response.LogoutResponse
import com.example.isyara.data.remote.response.RegisterResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.parseErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository private constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(email, password)

                if (response.status == "success") {
                    Result.Success(response)
                } else {
                    Result.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                val errorMessage = parseErrorMessage(e)
                Result.Error(errorMessage ?: "Error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(name, email, password)

                if (response.status == "success") {
                    Result.Success(response)
                } else {
                    Result.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                val errorMessage = parseErrorMessage(e)
                Result.Error(errorMessage ?: "Error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    suspend fun logout(token: String): Result<LogoutResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout("Bearer $token")
                if (response.message == "Logged out successfully") {
                    Result.Success(response)
                } else {
                    Result.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: IOException) {
                Result.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                val errorMessage = parseErrorMessage(e)
                Result.Error(errorMessage ?: "Error: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An unexpected error occurred: ${e.message}")
            }
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