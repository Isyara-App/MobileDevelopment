package com.example.isyara.ui.information

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.InformationRepository
import com.example.isyara.di.Injection

class InformationViewModelFactory(
    private val informationRepository: InformationRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformationViewModel::class.java)) {
            return InformationViewModel(informationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: InformationViewModelFactory? = null

        fun getInstance(context: Context): InformationViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: InformationViewModelFactory(
                    Injection.informationRepository(context),
                )
            }.also { instance = it }
    }
}