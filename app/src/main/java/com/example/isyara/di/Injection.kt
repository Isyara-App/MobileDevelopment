package com.example.isyara.di

import android.content.Context
import com.example.isyara.data.remote.retrofit.ApiConfig
import com.example.isyara.data.repository.AuthRepository
import com.example.isyara.data.repository.ContactRepository
import com.example.isyara.data.repository.DictionaryRepository
import com.example.isyara.data.repository.InformationRepository
import com.example.isyara.data.repository.QuizRepository
import com.example.isyara.data.repository.UserRepository

object Injection {

    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun dictionaryRepository(context: Context): DictionaryRepository {
        val apiService = ApiConfig.getApiService()
        return DictionaryRepository.getInstance(apiService)
    }

    fun informationRepository(context: Context): InformationRepository {
        val apiService = ApiConfig.getApiService()
        return InformationRepository.getInstance(apiService)
    }

    fun quizRepository(context: Context): QuizRepository {
        val apiService = ApiConfig.getApiService()
        return QuizRepository.getInstance(apiService)
    }

    fun contactRepository(context: Context): ContactRepository {
        val apiService = ApiConfig.getApiService()
        return ContactRepository.getInstance(apiService)
    }

    fun userRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }
}