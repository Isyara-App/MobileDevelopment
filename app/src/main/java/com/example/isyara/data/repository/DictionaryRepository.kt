package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DictionarySentenceResponse
import com.example.isyara.data.remote.response.DictionaryWordResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.parseErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class DictionaryRepository private constructor(private val apiService: ApiService) {

    suspend fun searchWord(token: String, query: String): Result<DictionaryWordResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchLetters("Bearer $token", query)

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

    suspend fun searchSentence(token: String, query: String): Result<DictionarySentenceResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchSentence("Bearer $token", query)

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
        private var instance: DictionaryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): DictionaryRepository =
            instance ?: synchronized(this) {
                instance ?: DictionaryRepository(apiService)
            }.also { instance = it }
    }
}