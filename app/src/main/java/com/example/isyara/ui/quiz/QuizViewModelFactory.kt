package com.example.isyara.ui.quiz

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.QuizRepository
import com.example.isyara.di.Injection

class QuizViewModelFactory(
    private val quizRepository: QuizRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: QuizViewModelFactory? = null

        fun getInstance(context: Context): QuizViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: QuizViewModelFactory(
                    Injection.quizRepository(context),
                )
            }.also { instance = it }
    }
}