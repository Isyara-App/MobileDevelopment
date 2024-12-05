package com.example.isyara.ui.homescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.data.remote.response.DataItem
import com.example.isyara.data.repository.InformationRepository
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val repository: InformationRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _news = MutableLiveData<List<DataItem>>()
    val news: LiveData<List<DataItem>> get() = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchAllNews(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getAllNews(token)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _news.value = result.data.data?.filterNotNull() ?: emptyList()
                }

                is Result.Error -> {
                    userPreferences.clearToken()
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
