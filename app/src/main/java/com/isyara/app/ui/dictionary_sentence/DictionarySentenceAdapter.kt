package com.isyara.app.ui.dictionary_sentence

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isyara.app.R
import com.isyara.app.data.remote.response.DataItemSentence
import com.isyara.app.databinding.DictionarySentenceListBinding
import com.isyara.app.util.LoadImage


class DictionarySentenceAdapter(
    private val words: List<DataItemSentence>,
    private val onToggleLearning: ((DataItemSentence, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<DictionarySentenceAdapter.SentenceViewHolder>() {

    // Track toggled states locally
    private val toggledStates = mutableMapOf<Int, Boolean>()

    inner class SentenceViewHolder(private val binding: DictionarySentenceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sentence: DataItemSentence) {
            binding.tvTitle.text = sentence.kata
            LoadImage.load(
                context = binding.root.context,
                imageView = binding.ivPicture,
                imageUrl = sentence.imageUrl ?: "",
                placeholder = R.color.placeholder,
            )

            val itemId = sentence.id ?: 0
            val isKnowing = toggledStates[itemId] ?: false
            updateToggleIcon(isKnowing)

            binding.btnToggleLearning.setOnClickListener {
                val newState = !(toggledStates[itemId] ?: false)
                toggledStates[itemId] = newState
                updateToggleIcon(newState)
                onToggleLearning?.invoke(sentence, newState)
            }
        }

        private fun updateToggleIcon(isKnowing: Boolean) {
            if (isKnowing) {
                binding.btnToggleLearning.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                binding.btnToggleLearning.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentenceViewHolder {
        val binding = DictionarySentenceListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SentenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SentenceViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int = words.size
}
