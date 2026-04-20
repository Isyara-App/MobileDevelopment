import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isyara.app.R
import com.isyara.app.data.remote.response.DataItemWord
import com.isyara.app.databinding.DictionaryWordListBinding
import com.isyara.app.util.LoadImage


class DictionaryWordAdapter(
    private val words: List<DataItemWord>,
    private val onToggleLearning: ((DataItemWord, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<DictionaryWordAdapter.WordViewHolder>() {

    // Track toggled states locally
    private val toggledStates = mutableMapOf<Int, Boolean>()

    inner class WordViewHolder(private val binding: DictionaryWordListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: DataItemWord) {
            binding.tvTitle.text = word.huruf
            LoadImage.load(
                context = binding.root.context,
                imageView = binding.ivPicture,
                imageUrl = word.imageUrl ?: "",
                placeholder = R.color.placeholder,
                keepFullImageVisible = true
            )

            val itemId = word.id ?: 0
            val isKnowing = toggledStates[itemId] ?: false
            updateToggleIcon(isKnowing)

            binding.btnToggleLearning.setOnClickListener {
                val newState = !(toggledStates[itemId] ?: false)
                toggledStates[itemId] = newState
                updateToggleIcon(newState)
                onToggleLearning?.invoke(word, newState)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = DictionaryWordListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int = words.size
}
