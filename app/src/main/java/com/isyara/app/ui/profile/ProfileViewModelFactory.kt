package com.isyara.app.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.data.repository.UserRepository
import com.isyara.app.di.Injection

class ProfileViewModelFactory(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.isyara.app.ui.profile.ProfileViewModel::class.java)) {
            return com.isyara.app.ui.profile.ProfileViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: com.isyara.app.ui.profile.ProfileViewModelFactory? = null

        fun getInstance(context: Context): com.isyara.app.ui.profile.ProfileViewModelFactory =
            com.isyara.app.ui.profile.ProfileViewModelFactory.Companion.instance ?: synchronized(this) {
                com.isyara.app.ui.profile.ProfileViewModelFactory.Companion.instance
                    ?: com.isyara.app.ui.profile.ProfileViewModelFactory(
                        Injection.userRepository(context),
                        UserPreferences(context)
                    )
            }.also { com.isyara.app.ui.profile.ProfileViewModelFactory.Companion.instance = it }
    }
}