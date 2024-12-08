package com.example.isyara.ui.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.data.remote.response.DataQuiz
import com.example.isyara.databinding.LevelListBinding

class QuizAdapter(
    private val levelItem: List<DataQuiz>,
    private val onItemClicked: (DataQuiz) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    inner class QuizViewHolder(private val binding: LevelListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(levelItem: DataQuiz) {
            binding.lvlText.text = levelItem.name
            binding.root.setOnClickListener {
                onItemClicked(levelItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = LevelListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return QuizViewHolder(binding)
    }

    override fun getItemCount(): Int = levelItem.size

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(levelItem[position])
    }
}