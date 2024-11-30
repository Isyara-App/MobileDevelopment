package com.example.isyara.ui.information_event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.R
import com.example.isyara.data.remote.response.DataEvent
import com.example.isyara.databinding.InformationListBinding
import com.example.isyara.util.LoadImage

class EventInformationAdapter(
    private val eventItem: List<DataEvent>,
    private val onItemClicked: (DataEvent) -> Unit
) : RecyclerView.Adapter<EventInformationAdapter.EventViewHolder>() {
    inner class EventViewHolder(private val binding: InformationListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(eventItem: DataEvent) {
            binding.itemTitle.text = eventItem.title
            binding.itemContent.text = eventItem.description

            LoadImage.load(
                context = binding.root.context,
                imageView = binding.itemImage,
                imageUrl = eventItem.imageUrl ?: "",
                placeholder = R.color.placeholder
            )

            binding.root.setOnClickListener {
                onItemClicked(eventItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = InformationListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventItem[position])
    }

    override fun getItemCount(): Int = eventItem.size

}