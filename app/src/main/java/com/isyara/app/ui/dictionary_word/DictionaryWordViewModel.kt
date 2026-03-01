package com.isyara.app.ui.dictionary_word

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyara.app.data.Result
import com.isyara.app.data.remote.response.DataItemWord
import com.isyara.app.data.repository.DictionaryRepository
import kotlinx.coroutines.launch

class DictionaryWordViewModel(private val repository: DictionaryRepository) : ViewModel() {

    private val _sentences = MutableLiveData<List<DataItemWord>>()
    val sentences: LiveData<List<DataItemWord>> get() = _sentences

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _toggleResult = MutableLiveData<Boolean>()
    val toggleResult: LiveData<Boolean> get() = _toggleResult

    fun searchSentence(token: String, query: String, isBisindo: String? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.searchWord(token, query, isBisindo)) {
                is Result.Success -> {
                    _isLoading.value = false
                    _sentences.value = result.data.data?.filterNotNull() ?: emptyList()
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

    fun toggleLearningStatus(token: String, id: Int, isKnowing: Boolean) {
        viewModelScope.launch {
            when (val result = repository.toggleLetterLearningStatus(token, id, isKnowing)) {
                is Result.Success -> {
                    _toggleResult.value = true
                }

                is Result.Error -> {
                    _errorMessage.value = result.error
                }

                is Result.Loading -> {}
            }
        }
    }
}
