package com.example.isyara.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.data.remote.response.LogoutResponse
import com.example.isyara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _logoutResult = MutableLiveData<Result<LogoutResponse>>()
    val logoutResult: LiveData<Result<LogoutResponse>> = _logoutResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun logout(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.logout(token)
                _logoutResult.value = result

                if (result is Result.Success) {
                    userPreferences.clearToken()
                } else if (result is Result.Error) {
                    _errorMessage.value = result.error
                }

            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}