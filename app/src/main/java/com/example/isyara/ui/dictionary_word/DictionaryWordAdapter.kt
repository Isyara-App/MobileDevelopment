import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.data.Word
import com.example.isyara.databinding.DictionaryWordListBinding


class DictionaryWordAdapter(private val words: List<Word>) :
    RecyclerView.Adapter<DictionaryWordAdapter.WordViewHolder>() {

    inner class WordViewHolder(private val binding: DictionaryWordListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.tvTitle.text = word.title
            binding.ivPicture.setImageResource(word.imageResId)
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
