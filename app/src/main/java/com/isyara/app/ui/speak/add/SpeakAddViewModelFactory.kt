package com.isyara.app.ui.speak.add

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.repository.SpeakRepository
import com.isyara.app.di.Injection

class SpeakAddViewModelFactory(private val repository: SpeakRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeakAddViewModel::class.java)) {
            return SpeakAddViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SpeakAddViewModelFactory? = null

        fun getInstance(context: Context): SpeakAddViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SpeakAddViewModelFactory(Injection.speakRepository(context))
            }.also { instance = it }
    }
}
