package com.example.isyara.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DataQuiz
import com.example.isyara.data.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {
    private val _levels = MutableLiveData<List<DataQuiz>>()
    val level: LiveData<List<DataQuiz>> get() = _levels

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchLevels(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getAllLevel(token)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _levels.value = result.data.data?.filterNotNull() ?: emptyList()
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

    fun clearError() {
        _errorMessage.value = ""
    }
}