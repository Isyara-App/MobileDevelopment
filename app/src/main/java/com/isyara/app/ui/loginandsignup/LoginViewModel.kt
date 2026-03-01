package com.isyara.app.ui.loginandsignup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyara.app.data.Result
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.data.remote.response.LoginResponse
import com.isyara.app.data.repository.AuthRepository
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
                    val token = loginResponse.accessToken
                    val name = loginResponse.user?.name
                    val id = loginResponse.user?.id.toString()
                    val image = loginResponse.user?.imageUrl

                    if (token != null && name != null) {
                        userPreferences.saveToken(token)
                        userPreferences.saveName(name)
                        userPreferences.saveId(id)
                        if (!image.isNullOrEmpty()) {
                            userPreferences.saveImage(image)
                        }
                        Log.d("LoginViewModel", "Token saved: $token, id : $id")
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
