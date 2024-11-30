package com.example.isyara.ui.information

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.R
import com.example.isyara.data.remote.response.DataCommunity
import com.example.isyara.databinding.InformationListBinding
import com.example.isyara.util.LoadImage

class InformationAdapter(
    private val communityItem: List<DataCommunity>,
    private val onItemClicked: (DataCommunity) -> Unit
) : RecyclerView.Adapter<InformationAdapter.CommunityViewHolder>() {
    inner class CommunityViewHolder(private val binding: InformationListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(communityItem: DataCommunity) {
            binding.itemTitle.text = communityItem.title
            binding.itemContent.text = communityItem.description

            LoadImage.load(
                context = binding.root.context,
                imageView = binding.itemImage,
                imageUrl = communityItem.imageUrl ?: "",
                placeholder = R.color.placeholder
            )

            binding.root.setOnClickListener {
                onItemClicked(communityItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = InformationListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CommunityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(communityItem[position])
    }

    override fun getItemCount(): Int = communityItem.size

}