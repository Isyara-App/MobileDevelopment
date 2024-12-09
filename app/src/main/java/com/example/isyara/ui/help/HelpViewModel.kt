package com.example.isyara.ui.help

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.repository.ContactRepository
import kotlinx.coroutines.launch

class HelpViewModel(private val repository: ContactRepository) :
    ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun sendMessage(token: String, name: String, email: String, message: String) {
        _isLoading.value = true

        viewModelScope.launch {
            when (val result = repository.sendContact(token, name, email, message)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _message.value = result.data.message ?: "Message sent successfully"
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