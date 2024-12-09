package com.example.isyara.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.data.remote.response.DataProfile
import com.example.isyara.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {


    private val _profile = MutableLiveData<DataProfile>()
    val profile: LiveData<DataProfile> get() = _profile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun update(
        token: String,
        id: Int,
        imageFile: File? = null,
        name: String,
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            Log.d("ProfileViewModel", "update called with token: $token, id: $id, name: $name")
            when (val result = repository.updateProfile(token, id, imageFile, name)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _profile.value = result.data.data!!
                    Log.d("ProfileViewModel", "Update successful: ${result.data}")
                    if (result.data.data.imageUrl != null && result.data.data.name != null) {
                        userPreferences.saveName(result.data.data.name)
                        userPreferences.saveImage(result.data.data.imageUrl)
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

}