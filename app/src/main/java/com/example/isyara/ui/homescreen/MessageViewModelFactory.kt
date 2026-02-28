package com.example.isyara.ui.homescreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.isyara.data.repository.MessageRepository
import com.example.isyara.di.Injection

class MessageViewModelFactory(
    private val messageRepository: MessageRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: MessageViewModelFactory? = null

        fun getInstance(context: Context): MessageViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MessageViewModelFactory(
                    Injection.messageRepository(context)
                )
            }.also { instance = it }
    }
}
