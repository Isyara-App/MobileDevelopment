package com.isyara.app.ui.speak

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.isyara.app.data.local.entity.PracticeItem
import com.isyara.app.databinding.ItemPracticeBinding

class SpeakAdapter(
    private val onItemClick: (PracticeItem) -> Unit,
    private val onDeleteClick: (PracticeItem) -> Unit
) : ListAdapter<PracticeItem, SpeakAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemPracticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PracticeItem) {
            binding.tvItemTargetText.text = item.targetText
            try {
                com.isyara.app.util.LoadImage.load(
                    context = binding.root.context,
                    imageView = binding.ivItemImage,
                    imageUrl = item.imageUri,
                    placeholder = android.R.color.darker_gray
                )
            } catch (e: Exception) {
                binding.ivItemImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PracticeItem>() {
            override fun areItemsTheSame(oldItem: PracticeItem, newItem: PracticeItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PracticeItem, newItem: PracticeItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
