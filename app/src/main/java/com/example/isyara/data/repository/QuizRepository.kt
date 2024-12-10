package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.CheckAnswerResponse
import com.example.isyara.data.remote.response.CheckCompletionResponse
import com.example.isyara.data.remote.response.QuestionResponse
import com.example.isyara.data.remote.response.QuizByIdResponse
import com.example.isyara.data.remote.response.QuizResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.safeApiCall

class QuizRepository private constructor(private val apiService: ApiService) {
    suspend fun getAllLevel(token: String): Result<QuizResponse> {
        return safeApiCall {
            apiService.allLevels("Bearer $token")
        }
    }

    suspend fun getLevelById(token: String, levelId: Int): Result<QuizByIdResponse> {
        return safeApiCall {
            apiService.levelById("Bearer $token", levelId)
        }
    }

    suspend fun getQuestionById(
        token: String,
        levelId: Int,
        questionId: Int
    ): Result<QuestionResponse> {
        return safeApiCall {
            apiService.questionById("Bearer $token", levelId, questionId)
        }
    }

    suspend fun checkAnswerById(
        token: String,
        levelId: Int,
        questionId: Int,
        selectedOption: ApiService.SelectedOptionRequest
    ): Result<CheckAnswerResponse> {
        return safeApiCall {
            apiService.checkAnswerById("Bearer $token", levelId, questionId, selectedOption)
        }
    }

    suspend fun checkCompletionById(token: String, levelId: Int): Result<CheckCompletionResponse> {
        return safeApiCall {
            apiService.checkCompletionById("Bearer $token", levelId)
        }
    }

    companion object {
        @Volatile
        private var instance: QuizRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): QuizRepository =
            instance ?: synchronized(this) {
                instance ?: QuizRepository(apiService)
            }.also { instance = it }
    }
}