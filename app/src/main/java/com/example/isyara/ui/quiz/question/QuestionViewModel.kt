package com.example.isyara.ui.quiz.question

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.CheckAnswerResponse
import com.example.isyara.data.remote.response.CheckCompletionResponse
import com.example.isyara.data.remote.response.QuestionResponse
import com.example.isyara.data.remote.retrofit.ApiService
import com.example.isyara.data.repository.QuizRepository
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
    private var isQuizCompleted: Boolean = false

    private var selectedOption: String? = null
    var totalQuestions: Int = 0

    fun fetchQuestionById(token: String, levelId: Int, questionId: Int) {
        currentLevelId = levelId
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getQuestionById(token, levelId, questionId)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _question.value = result.data
                    val name = result.data.data?.name ?: ""
                    val parts = name.split(" of ")

                    if (parts.size == 2) {
                        val total =
                            parts[1].toIntOrNull() // Konversi bagian kedua (total question) menjadi angka
                        totalQuestions =
                            total ?: 0 // Set nilai totalQuestions, default ke 0 jika gagal
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
                            _checkAnswer.value = result.data


                            Log.d("QuestionViewModel", "isCorrect : ${result.data.isCorrect}")
                            // Jika jawaban benar, muat pertanyaan selanjutnya
                            if (result.data.isCorrect == true) {
                                currentQuestionId = getNextQuestionId()
                                Log.d("QuestionViewModel", "quizstatus : $isQuizCompleted")
                                // Muat pertanyaan selanjutnya jika belum selesai
                                if (isQuizCompleted) {
                                    // Panggil check completion untuk mengecek status quiz
                                    checkQuizCompletion(token, levelId)
                                }
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
                    Log.d("QuestionViewModel", result.data.message!!)
                    isQuizCompleted =
                        result.data.message == "Congrats! Kamu telah menyelesaikan quiz Level 1. Silahkan lanjutkan perjalanan mu!"
                    Log.d("QuestionViewModel", "quizstatus2 : $isQuizCompleted")
                }

                is Result.Error -> {
                    _errorMessage.value = result.error
                }

                else -> {}
            }
        }
    }

    fun getNextQuestionId(): Int {
        return currentQuestionId + 1
    }

    fun isQuizCompleted(): Boolean {
        return currentQuestionId >= totalQuestions
    }

    fun selectOption(option: String) {
        selectedOption = option
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}