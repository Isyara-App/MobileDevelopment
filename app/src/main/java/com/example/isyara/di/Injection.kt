package com.example.isyara.di

import android.content.Context
import com.example.isyara.data.remote.retrofit.ApiConfig
import com.example.isyara.data.repository.AuthRepository
import com.example.isyara.data.repository.DictionaryRepository
import com.example.isyara.data.repository.MessageRepository
import com.example.isyara.data.repository.QuizRepository
import com.example.isyara.data.repository.SpeakRepository
import com.example.isyara.data.repository.UserRepository
import com.example.isyara.data.local.room.SpeakDatabase

object Injection {

    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun dictionaryRepository(context: Context): DictionaryRepository {
        val apiService = ApiConfig.getApiService()
        return DictionaryRepository.getInstance(apiService)
    }

    fun quizRepository(context: Context): QuizRepository {
        val apiService = ApiConfig.getApiService()
        return QuizRepository.getInstance(apiService)
    }

    fun userRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }

    fun messageRepository(context: Context): MessageRepository {
        val apiService = ApiConfig.getApiService()
        return MessageRepository.getInstance(apiService)
    }

    fun speakRepository(context: Context): SpeakRepository {
        val database = SpeakDatabase.getDatabase(context)
        return SpeakRepository.getInstance(database.practiceItemDao())
    }
}