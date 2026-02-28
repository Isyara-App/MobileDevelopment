package com.example.isyara.ui.homescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.MessageData
import com.example.isyara.data.repository.MessageRepository
import kotlinx.coroutines.launch

class MessageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _messages = MutableLiveData<List<MessageData>>()
    val messages: LiveData<List<MessageData>> = _messages

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _sendResult = MutableLiveData<Boolean>()
    val sendResult: LiveData<Boolean> = _sendResult

    fun fetchMessages(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = messageRepository.getMessages(token)
            _isLoading.value = false
            when (result) {
                is Result.Success -> {
                    _messages.value = result.data.data ?: emptyList()
                }
                is Result.Error -> {
                    _errorMessage.value = result.error
                }
                is Result.Loading -> {}
            }
        }
    }

    fun sendMessage(token: String, message: String) {
        viewModelScope.launch {
            val result = messageRepository.sendMessage(token, message)
            when (result) {
                is Result.Success -> {
                    _sendResult.value = true
                    fetchMessages(token)
                }
                is Result.Error -> {
                    _errorMessage.value = result.error
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
