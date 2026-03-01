package com.isyara.app.data.repository

import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.CheckAnswerResponse
import com.isyara.app.data.remote.response.CheckCompletionResponse
import com.isyara.app.data.remote.response.QuestionResponse
import com.isyara.app.data.remote.response.QuizByIdResponse
import com.isyara.app.data.remote.response.QuizResponse
import com.isyara.app.data.remote.response.ScoreDetailResponse
import com.isyara.app.data.remote.response.ScoreListResponse
import com.isyara.app.data.remote.retrofit.ApiService
import com.isyara.app.util.safeApiCall

class QuizRepository private constructor(private val apiService: ApiService) {
    suspend fun getAllLevel(token: String): Result<QuizResponse> {
        return safeApiCall {
            apiService.allLevels("Bearer $token")
        }
    }

    suspend fun getLevelById(token: String, levelId: Int): Result<com.isyara.app.data.remote.response.QuizByIdResponse> {
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

    suspend fun getScores(token: String): Result<ScoreListResponse> {
        return safeApiCall {
            apiService.getScores("Bearer $token")
        }
    }

    suspend fun getScoreByLevel(token: String, levelId: Int): Result<ScoreDetailResponse> {
        return safeApiCall {
            apiService.getScoreByLevel("Bearer $token", levelId)
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