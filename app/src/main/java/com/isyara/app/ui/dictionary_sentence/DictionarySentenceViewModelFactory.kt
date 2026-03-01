package com.isyara.app.ui.dictionary_sentence

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.data.repository.DictionaryRepository
import com.isyara.app.di.Injection

class DictionarySentenceViewModelFactory(
    private val dictionaryRepository: DictionaryRepository,
    private val userPreferences: UserPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionarySentenceViewModel::class.java)) {
            return DictionarySentenceViewModel(dictionaryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DictionarySentenceViewModelFactory? = null

        fun getInstance(context: Context): DictionarySentenceViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DictionarySentenceViewModelFactory(
                    Injection.dictionaryRepository(context),
                    UserPreferences(context)
                )
            }.also { instance = it }
    }
}