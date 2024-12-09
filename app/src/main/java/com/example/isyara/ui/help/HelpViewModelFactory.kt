package com.example.isyara.ui.help

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.ContactRepository
import com.example.isyara.di.Injection


class HelpViewModelFactory(
    private val contactRepository: ContactRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HelpViewModel::class.java)) {
            return HelpViewModel(contactRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: HelpViewModelFactory? = null

        fun getInstance(context: Context): HelpViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HelpViewModelFactory(
                    Injection.contactRepository(context),
                )
            }.also { instance = it }
    }
}