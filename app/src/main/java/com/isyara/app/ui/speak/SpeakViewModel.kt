package com.isyara.app.ui.speak

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isyara.app.data.local.entity.PracticeItem
import com.isyara.app.data.repository.SpeakRepository
import kotlinx.coroutines.launch

class SpeakViewModel(
    private val repository: SpeakRepository,
    private val prefer: com.isyara.app.data.pref.UserPreferences
) : ViewModel() {

    private val _practiceItems = MutableLiveData<List<PracticeItem>>()
    val practiceItems: LiveData<List<PracticeItem>> get() = _practiceItems

    fun fetchPracticeItems() {
        viewModelScope.launch {
            if (!prefer.isSpeakDefaultInjected()) {
                repository.insertPracticeItem(PracticeItem(imageUri = "https://images.unsplash.com/photo-1518780664697-55e3ad937233?auto=format&fit=crop&q=80&w=800", targetText = "RUMAH"))
                repository.insertPracticeItem(PracticeItem(imageUri = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&q=80&w=800", targetText = "SUSU"))
                repository.insertPracticeItem(PracticeItem(imageUri = "https://images.unsplash.com/photo-1607378112100-4c6a10ec4d50?auto=format&fit=crop&q=80&w=800", targetText = "RUSA"))
                prefer.setSpeakDefaultInjected(true)
            }
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
