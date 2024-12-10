package com.example.isyara.ui.quiz.question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.R
import com.example.isyara.databinding.QuestionListBinding

class QuestionAdapter(
    private val options: List<String?>,
    private val onOptionSelected: (String) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.OptionViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    class OptionViewHolder(
        private val binding: QuestionListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            option: String?,
            isSelected: Boolean,
            onOptionSelected: (String, Int) -> Unit
        ) {
            binding.optionText.text = option

            // Ubah warna latar belakang berdasarkan status pilihan
            binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (isSelected) R.color.placeholder
                    else R.color.white
                )
            )

            binding.root.setOnClickListener {
                // Kirim option dan posisi aktual saat ini
                onOptionSelected(option ?: "", adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.bind(
            option,
            // Gunakan holder.adapterPosition untuk memeriksa selected position
            holder.adapterPosition == selectedPosition,
            onOptionSelected = { selectedOption, actualPosition ->
                // Update posisi yang dipilih menggunakan posisi aktual
                val previousPosition = selectedPosition
                selectedPosition = actualPosition

                // Notify item berubah untuk memperbarui warna
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Panggil callback untuk memilih option
                onOptionSelected(selectedOption)
            }
        )
    }

    // Method dan override lainnya tetap sama
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = QuestionListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OptionViewHolder(binding)
    }

    override fun getItemCount(): Int = options.size
}