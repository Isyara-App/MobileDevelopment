package com.isyara.app.ui.quiz.question

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.CheckAnswerResponse
import com.isyara.app.data.remote.response.CheckCompletionResponse
import com.isyara.app.data.remote.response.QuestionResponse
import com.isyara.app.data.remote.retrofit.ApiService
import com.isyara.app.data.repository.QuizRepository
import kotlinx.coroutines.launch

class QuestionViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _question = MutableLiveData<QuestionResponse>()
    val question: LiveData<QuestionResponse> get() = _question

    private val _checkAnswer = MutableLiveData<CheckAnswerResponse>()
    val checkAnswer: LiveData<CheckAnswerResponse> get() = _checkAnswer

    private val _checkCompletion = MutableLiveData<CheckCompletionResponse>()
    val checkCompletion: LiveData<CheckCompletionResponse> get() = _checkCompletion

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var currentLevelId: Int? = null
    private var currentQuestionId: Int = 1

    private var selectedOption: String? = null
    var totalQuestions: Int = 0

    fun fetchQuestionById(token: String, levelId: Int, questionId: Int) {
        currentLevelId = levelId
        currentQuestionId = questionId
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getQuestionById(token, levelId, questionId)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _question.value = result.data
                    val name = result.data.data?.name ?: ""
                    val parts = name.split(" of ")

                    if (parts.size == 2) {
                        val total = parts[1].toIntOrNull()
                        totalQuestions = total ?: 0
                        Log.d("QuestionViewModel", "Total Questions: $totalQuestions")
                    }
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.error
                }

                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun checkAnswer(token: String) {
        currentLevelId?.let { levelId ->
            if (selectedOption != null) {
                _isLoading.value = true
                Log.d("QuestionViewModel", "Selected option: $selectedOption")
                Log.d("QuestionViewModel", "Question Id: $currentQuestionId")
                viewModelScope.launch {
                    when (val result = repository.checkAnswerById(
                        token,
                        levelId,
                        currentQuestionId,
                        ApiService.SelectedOptionRequest(selectedOption!!)
                    )) {
                        is Result.Success -> {
                            _isLoading.value = false
                            // Always advance the question counter
                            currentQuestionId++
                            selectedOption = null
                            _checkAnswer.value = result.data
                            Log.d("QuestionViewModel", "isCorrect : ${result.data.isCorrect}, score: ${result.data.score}")
                        }

                        is Result.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = result.error
                        }

                        is Result.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
            } else {
                _errorMessage.value = "Silakan pilih jawaban terlebih dahulu"
            }
        }
    }

    fun checkQuizCompletion(token: String, levelId: Int) {
        viewModelScope.launch {
            when (val result = repository.checkCompletionById(token, levelId)) {
                is Result.Success -> {
                    _checkCompletion.value = result.data
                    Log.d("QuestionViewModel", "Completion: ${result.data.message}")
                }

                is Result.Error -> {
                    _errorMessage.value = result.error
                }

                else -> {}
            }
        }
    }

    fun getNextQuestionId(): Int {
        return currentQuestionId
    }

    fun selectOption(option: String) {
        selectedOption = option
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}