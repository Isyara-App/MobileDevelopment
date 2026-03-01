package com.isyara.app.ui.dictionary_word

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.data.repository.DictionaryRepository
import com.isyara.app.di.Injection

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