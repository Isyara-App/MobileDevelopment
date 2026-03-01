package com.isyara.app.ui.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.isyara.app.data.remote.response.MessageData
import com.isyara.app.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MessageAdapter : ListAdapter<MessageData, MessageAdapter.MessageViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageData) {
            binding.tvMessage.text = message.message ?: ""
            binding.tvTime.text = formatTime(message.createdAt)
        }

        private fun formatTime(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return ""
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                val date = inputFormat.parse(dateString)
                if (date != null) outputFormat.format(date) else dateString
            } catch (e: Exception) {
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                    val date = inputFormat.parse(dateString)
                    if (date != null) outputFormat.format(date) else dateString
                } catch (e2: Exception) {
                    dateString
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MessageData>() {
            override fun areItemsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
                return oldItem == newItem
            }
        }
    }
}
