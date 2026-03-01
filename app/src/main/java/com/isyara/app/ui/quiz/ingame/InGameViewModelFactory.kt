package com.isyara.app.ui.quiz.ingame

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isyara.app.data.repository.QuizRepository
import com.isyara.app.di.Injection

class InGameViewModelFactory(
    private val quizRepository: QuizRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InGameViewModel::class.java)) {
            return InGameViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: InGameViewModelFactory? = null

        fun getInstance(context: Context): InGameViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: InGameViewModelFactory(
                    Injection.quizRepository(context),
                )
            }.also { instance = it }
    }
}