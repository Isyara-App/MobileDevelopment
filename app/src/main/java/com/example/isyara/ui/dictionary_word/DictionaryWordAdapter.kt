import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.R
import com.example.isyara.data.remote.response.DataItemWord
import com.example.isyara.databinding.DictionarySentenceListBinding
import com.example.isyara.util.LoadImage


class DictionaryWordAdapter(
    private val words: List<DataItemWord>
) : RecyclerView.Adapter<DictionaryWordAdapter.SentenceViewHolder>() {

    inner class SentenceViewHolder(private val binding: DictionarySentenceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sentence: DataItemWord) {
            binding.tvTitle.text = sentence.huruf
            LoadImage.load(
                context = binding.root.context,
                imageView = binding.ivPicture,
                imageUrl = sentence.imageUrl ?: "",
                placeholder = R.color.placeholder,
            )
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
