package com.example.isyara.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.data.repository.AuthRepository
import com.example.isyara.di.Injection

class SettingsViewModelFactory(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(authRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SettingsViewModelFactory? = null

        fun getInstance(context: Context): SettingsViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SettingsViewModelFactory(
                    Injection.authRepository(context),
                    UserPreferences(context)
                )
            }.also { instance = it }
    }
}