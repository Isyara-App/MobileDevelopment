package com.example.isyara.ui.quiz.question

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.QuizRepository
import com.example.isyara.di.Injection

class QuestionViewModelFactory(
    private val quizRepository: QuizRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            return QuestionViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: QuestionViewModelFactory? = null

        fun getInstance(context: Context): QuestionViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: QuestionViewModelFactory(
                    Injection.quizRepository(context),
                )
            }.also { instance = it }
    }
}