package com.isyara.app.data.repository

import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.DictionarySentenceResponse
import com.isyara.app.data.remote.response.DictionaryWordResponse
import com.isyara.app.data.remote.response.LearningStatusResponse
import com.isyara.app.data.remote.retrofit.ApiService
import com.isyara.app.util.safeApiCall

class DictionaryRepository private constructor(private val apiService: ApiService) {

    suspend fun searchWord(
        token: String,
        query: String,
        isBisindo: String? = null
    ): Result<DictionaryWordResponse> {
        return safeApiCall {
            apiService.searchLetters("Bearer $token", query, isBisindo)
        }
    }

    suspend fun searchSentence(
        token: String,
        query: String,
        isBisindo: String? = null
    ): Result<DictionarySentenceResponse> {
        return safeApiCall {
            apiService.searchSentence("Bearer $token", query, isBisindo)
        }
    }

    suspend fun toggleLetterLearningStatus(
        token: String,
        id: Int,
        isKnowing: Boolean
    ): Result<com.isyara.app.data.remote.response.LearningStatusResponse> {
        return safeApiCall {
            apiService.toggleLetterLearningStatus(
                "Bearer $token",
                id,
                ApiService.LearningStatusRequest(isKnowing)
            )
        }
    }

    suspend fun toggleWordLearningStatus(
        token: String,
        id: Int,
        isKnowing: Boolean
    ): Result<com.isyara.app.data.remote.response.LearningStatusResponse> {
        return safeApiCall {
            apiService.toggleWordLearningStatus(
                "Bearer $token",
                id,
                ApiService.LearningStatusRequest(isKnowing)
            )
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