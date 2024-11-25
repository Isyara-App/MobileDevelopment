package com.example.isyara.ui.dictionary_word

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.data.repository.DictionaryRepository
import com.example.isyara.di.Injection

class DictionaryWordViewModelFactory(
    private val dictionaryRepository: DictionaryRepository,
    private val userPreferences: UserPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionaryWordViewModel::class.java)) {
            return DictionaryWordViewModel(dictionaryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DictionaryWordViewModelFactory? = null

        fun getInstance(context: Context): DictionaryWordViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DictionaryWordViewModelFactory(
                    Injection.dictionaryRepository(context),
                    UserPreferences(context)
                )
            }.also { instance = it }
    }
}