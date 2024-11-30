package com.example.isyara.ui.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DataCommunity
import com.example.isyara.data.repository.InformationRepository
import kotlinx.coroutines.launch

class InformationViewModel(private val repository: InformationRepository) : ViewModel() {
    private val _community = MutableLiveData<List<DataCommunity>>()
    val community: LiveData<List<DataCommunity>> get() = _community

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchCommunity(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getCommunity(token)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _community.value = result.data.data?.filterNotNull() ?: emptyList()
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