package com.example.isyara.ui.score

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.QuizRepository
import com.example.isyara.di.Injection

class ScoreViewModelFactory(
    private val quizRepository: QuizRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(quizRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ScoreViewModelFactory? = null

        fun getInstance(context: Context): ScoreViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ScoreViewModelFactory(
                    Injection.quizRepository(context),
                )
            }.also { instance = it }
    }
}
