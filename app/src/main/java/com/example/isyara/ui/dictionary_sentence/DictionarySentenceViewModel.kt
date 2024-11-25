package com.example.isyara.ui.dictionary_sentence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.Result
import com.example.isyara.data.remote.response.DataItem
import com.example.isyara.data.repository.DictionaryRepository
import kotlinx.coroutines.launch

class DictionarySentenceViewModel(private val repository: DictionaryRepository) : ViewModel() {

    private val _sentences = MutableLiveData<List<DataItem>>()
    val sentences: LiveData<List<DataItem>> get() = _sentences

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun searchSentence(token: String, query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.searchSentence(token, query)) {
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
}
