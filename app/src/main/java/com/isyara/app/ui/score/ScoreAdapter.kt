package com.isyara.app.ui.score

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isyara.app.data.remote.response.UserProgressData
import com.isyara.app.databinding.ItemScoreBinding

class ScoreAdapter(
    private val scores: List<UserProgressData>
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    inner class ScoreViewHolder(private val binding: ItemScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(score: UserProgressData) {
            binding.tvLevelName.text = "Level ${score.levelId ?: "-"}"
            binding.tvScore.text = "Score: ${score.score ?: 0}"
            binding.tvStatus.text = score.status ?: "-"
            binding.tvCorrectAnswers.text = "${score.correctAnswers ?: 0}/${score.totalQuestions ?: 0} Benar"

            // Change status color based on status
            val statusColor = when (score.status?.lowercase()) {
                "completed" -> android.graphics.Color.parseColor("#ADFFFFFF")
                "in_progress" -> android.graphics.Color.parseColor("#FFFFCC80")
                else -> android.graphics.Color.parseColor("#88FFFFFF")
            }
            binding.tvStatus.setTextColor(statusColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemScoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scores[position])
    }

    override fun getItemCount(): Int = scores.size
}
