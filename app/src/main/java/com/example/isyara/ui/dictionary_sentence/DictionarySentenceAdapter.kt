import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.isyara.data.Sentence
import com.example.isyara.databinding.DictionarySentenceListBinding


class DictionarySentenceAdapter(private val sentences: List<Sentence>) :
    RecyclerView.Adapter<DictionarySentenceAdapter.SentenceViewHolder>() {

    inner class SentenceViewHolder(private val binding: DictionarySentenceListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sentence: Sentence) {
            binding.tvTitle.text = sentence.title
            binding.ivPicture.setImageResource(sentence.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentenceViewHolder {
        val binding = DictionarySentenceListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SentenceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SentenceViewHolder, position: Int) {
        holder.bind(sentences[position])
    }

    override fun getItemCount(): Int = sentences.size
}
