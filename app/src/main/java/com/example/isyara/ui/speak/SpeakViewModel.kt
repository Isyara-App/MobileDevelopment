package com.example.isyara.ui.speak

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.isyara.data.local.entity.PracticeItem
import com.example.isyara.data.repository.SpeakRepository
import kotlinx.coroutines.launch

class SpeakViewModel(private val repository: SpeakRepository) : ViewModel() {

    private val _practiceItems = MutableLiveData<List<PracticeItem>>()
    val practiceItems: LiveData<List<PracticeItem>> get() = _practiceItems

    fun fetchPracticeItems() {
        viewModelScope.launch {
            val items = repository.getAllPracticeItems()
            _practiceItems.postValue(items)
        }
    }

    fun deletePracticeItem(item: PracticeItem) {
        viewModelScope.launch {
            repository.deletePracticeItem(item)
            fetchPracticeItems() // Refresh list after deletion
        }
    }
}
