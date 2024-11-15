package com.example.isyara.ui.homescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.R
import com.example.isyara.data.InformationSample

class HomeScreenAdapter (private val items: List<InformationSample>) :
    RecyclerView.Adapter<HomeScreenAdapter.InformationViewHolder>() {

    inner class InformationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.item_title)
        val textDescription: TextView = itemView.findViewById(R.id.item_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.information_list, parent, false)
        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        val item = items[position]
        holder.textTitle.text = item.title
        holder.textDescription.text = item.description
    }

    override fun getItemCount(): Int = items.size
}