package com.example.isyara.di

import android.content.Context
import com.example.isyara.data.remote.retrofit.ApiConfig
import com.example.isyara.data.repository.AuthRepository
import com.example.isyara.data.repository.DictionaryRepository

object Injection {

    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun dictionaryRepository(context: Context): DictionaryRepository {
        val apiService = ApiConfig.getApiService()
        return DictionaryRepository.getInstance(apiService)
    }
}