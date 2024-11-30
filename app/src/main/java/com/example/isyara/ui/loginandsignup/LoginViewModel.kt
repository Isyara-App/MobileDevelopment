package com.example.isyara.ui.loginandsignup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.data.remote.response.LoginResponse
import com.example.isyara.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                _loginResult.value = result

                if (result is Result.Success) {
                    val loginResponse = result.data
                    val token = loginResponse.token
                    val name = loginResponse.data?.name

                    if (token != null && name != null) {
                        userPreferences.saveToken(token)
                        userPreferences.saveName(name)
                    }

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
