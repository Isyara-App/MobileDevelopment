package com.isyara.app.ui.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.UserProgressData
import com.isyara.app.data.repository.QuizRepository
import kotlinx.coroutines.launch

class ScoreViewModel(private val repository: QuizRepository) : ViewModel() {

    private val _scores = MutableLiveData<List<UserProgressData>>()
    val scores: LiveData<List<UserProgressData>> get() = _scores

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchScores(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getScores(token)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _scores.value = result.data.data ?: emptyList()
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
