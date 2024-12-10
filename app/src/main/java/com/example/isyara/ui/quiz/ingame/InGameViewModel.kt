package com.example.isyara.ui.quiz.ingame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DataQuizById
import com.example.isyara.data.repository.QuizRepository
import kotlinx.coroutines.launch

class InGameViewModel(private val quizRepository: QuizRepository) : ViewModel() {

    private val _levelData = MutableLiveData<DataQuizById?>()
    val levelData: LiveData<DataQuizById?> get() = _levelData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchLevelById(token: String, levelId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = quizRepository.getLevelById(token, levelId)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _levelData.value = result.data.data
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
}