package com.isyara.app.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyara.app.data.Result
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.data.remote.response.DataProfile
import com.isyara.app.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _profile = MutableLiveData<DataProfile>()
    val profile: LiveData<DataProfile> get() = _profile

    private val _photo = MutableLiveData<String>()
    val photo: LiveData<String> get() = _photo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _deleteAccountResult = MutableLiveData<Boolean>()
    val deleteAccountResult: LiveData<Boolean> get() = _deleteAccountResult

    fun fetchProfile(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getProfile(token)) {
                is Result.Success -> {
                    _isLoading.value = false
                    result.data.data?.let { profileData ->
                        _profile.value = profileData
                        profileData.name?.let { userPreferences.saveName(it) }
                        profileData.imageUrl?.let {
                            userPreferences.saveImage(it)
                            _photo.value = it
                        }
                    }
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

    fun update(
        token: String,
        imageFile: File? = null,
        name: String,
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            Log.d("ProfileViewModel", "update called with token: $token, name: $name")
            when (val result = repository.updateProfile(token, imageFile, name)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _profile.value = result.data.data!!
                    Log.d("ProfileViewModel", "Update successful: ${result.data}")
                    if (result.data.data.imageUrl != null && result.data.data.name != null) {
                        userPreferences.saveName(result.data.data.name)
                        userPreferences.saveImage(result.data.data.imageUrl)
                        _photo.value = result.data.data.imageUrl!!
                    }

                }

                is Result.Error -> {
                    _isLoading.value = false
                    Log.d("ProfileViewModel", "Error occurred: ${result.error}")
                    _errorMessage.value = result.error
                }

                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun deleteAccount(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.deleteAccount(token)) {
                is Result.Success -> {
                    _isLoading.value = false
                    userPreferences.clearToken()
                    _deleteAccountResult.value = true
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