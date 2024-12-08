package com.example.isyara.data.repository

import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.CommunityResponse
import com.example.isyara.data.remote.response.EventResponse
import com.example.isyara.data.remote.response.NewsResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.util.safeApiCall

class InformationRepository private constructor(private val apiService: ApiService) {
    suspend fun getAllNews(token: String): Result<NewsResponse> {
        return safeApiCall {
            apiService.news("Bearer $token")
        }
    }

    suspend fun getEvents(token: String): Result<EventResponse> {
        return safeApiCall {
            apiService.events("Bearer $token")
        }
    }

    suspend fun getCommunity(token: String): Result<CommunityResponse> {
        return safeApiCall {
            apiService.community("Bearer $token")
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