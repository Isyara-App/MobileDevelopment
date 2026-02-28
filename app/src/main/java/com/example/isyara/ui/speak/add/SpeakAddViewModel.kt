package com.example.isyara.ui.speak.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.local.entity.PracticeItem
import com.example.isyara.data.repository.SpeakRepository
import kotlinx.coroutines.launch

class SpeakAddViewModel(private val repository: SpeakRepository) : ViewModel() {

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> get() = _isSaved

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun savePracticeItem(imageUri: String, targetText: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val item = PracticeItem(
                    imageUri = imageUri,
                    targetText = targetText.trim()
                )
                repository.insertPracticeItem(item)
                _isSaved.value = true
            } catch (e: Exception) {
                _isSaved.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}
