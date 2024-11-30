package com.example.isyara.ui.information_event

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.InformationRepository
import com.example.isyara.di.Injection


class EventInformationViewModelFactory(
    private val informationRepository: InformationRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventInformationViewModel::class.java)) {
            return EventInformationViewModel(informationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: EventInformationViewModelFactory? = null

        fun getInstance(context: Context): EventInformationViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: EventInformationViewModelFactory(
                    Injection.informationRepository(context),
                )
            }.also { instance = it }
    }
}