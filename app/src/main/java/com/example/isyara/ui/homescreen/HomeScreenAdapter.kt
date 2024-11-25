package com.example.isyara.ui.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.R
import com.example.isyara.data.remote.response.DataItem
import com.example.isyara.databinding.InformationListBinding
import com.example.isyara.util.LoadImage

class HomeScreenAdapter(
    private val newsItems: List<DataItem>,
    private val onItemClicked: (DataItem) -> Unit
) : RecyclerView.Adapter<HomeScreenAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: InformationListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(newsItem: DataItem) {
            binding.itemTitle.text = newsItem.title
            binding.itemContent.text = newsItem.description

            // Load image with utility
            LoadImage.load(
                context = binding.root.context,
                imageView = binding.itemImage,
                imageUrl = newsItem.imageUrl ?: "",
                placeholder = R.color.placeholder
            )

            // Click listener for item
            binding.root.setOnClickListener {
                onItemClicked(newsItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = InformationListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsItems[position])
    }

    override fun getItemCount(): Int = newsItems.size
}
