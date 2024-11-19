package com.example.isyara.di

import android.content.Context
import com.example.isyara.data.remote.retrofit.ApiConfig
import com.example.isyara.data.repository.AuthRepository

object Injection {

    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }
}