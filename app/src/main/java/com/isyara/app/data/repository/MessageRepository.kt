package com.isyara.app.data.repository

import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.MessageListResponse
import com.isyara.app.data.remote.response.SendMessageResponse
import com.isyara.app.data.remote.retrofit.ApiService
import com.isyara.app.util.safeApiCall

class MessageRepository private constructor(private val apiService: ApiService) {

    suspend fun getMessages(token: String): Result<MessageListResponse> {
        return safeApiCall {
            apiService.getMessages("Bearer $token")
        }
    }

    suspend fun sendMessage(token: String, message: String): Result<SendMessageResponse> {
        return safeApiCall {
            apiService.sendMessage(
                "Bearer $token",
                ApiService.SendMessageRequest(message)
            )
        }
    }

    companion object {
        @Volatile
        private var instance: MessageRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): MessageRepository =
            instance ?: synchronized(this) {
                instance ?: MessageRepository(apiService)
            }.also { instance = it }
    }
}
