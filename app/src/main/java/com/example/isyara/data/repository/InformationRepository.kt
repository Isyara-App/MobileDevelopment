package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.NewsResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.parseErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class InformationRepository private constructor(private val apiService: ApiService) {
    suspend fun getAllNews(token: String): Result<NewsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.news("Bearer $token")

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

    companion object {
        @Volatile
        private var instance: InformationRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): InformationRepository =
            instance ?: synchronized(this) {
                instance ?: InformationRepository(apiService)
            }.also { instance = it }
    }
}