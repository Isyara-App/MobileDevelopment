package com.example.isyara.ui.homescreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.InformationRepository
import com.example.isyara.di.Injection

class HomeScreenViewModelFactory(
    private val informationRepository: InformationRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            return HomeScreenViewModel(informationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: HomeScreenViewModelFactory? = null

        fun getInstance(context: Context): HomeScreenViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HomeScreenViewModelFactory(
                    Injection.informationRepository(context),
                )
            }.also { instance = it }
    }
}