package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DictionarySentenceResponse
import com.example.isyara.data.remote.response.DictionaryWordResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.safeApiCall

class DictionaryRepository private constructor(private val apiService: ApiService) {

    suspend fun searchWord(token: String, query: String): Result<DictionaryWordResponse> {
        return safeApiCall {
            apiService.searchLetters("Bearer $token", query)
        }
    }

    suspend fun searchSentence(token: String, query: String): Result<DictionarySentenceResponse> {
        return safeApiCall {
            apiService.searchSentence("Bearer $token", query)
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