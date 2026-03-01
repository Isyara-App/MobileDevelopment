package com.isyara.app.ui.loginandsignup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.repository.AuthRepository
import com.isyara.app.di.Injection

class SignUpViewModelFactory(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SignUpViewModelFactory? = null

        fun getInstance(context: Context): SignUpViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SignUpViewModelFactory(Injection.authRepository(context))
            }.also { instance = it }
    }
}