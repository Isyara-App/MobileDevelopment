package com.isyara.app.ui.speak

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.repository.SpeakRepository
import com.isyara.app.di.Injection

class SpeakViewModelFactory(private val repository: SpeakRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeakViewModel::class.java)) {
            return SpeakViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SpeakViewModelFactory? = null

        fun getInstance(context: Context): SpeakViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SpeakViewModelFactory(Injection.speakRepository(context))
            }.also { instance = it }
    }
}
