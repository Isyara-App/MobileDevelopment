package com.example.isyara.ui.dictionary_sentence

import DictionarySentenceAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isyara.R
import com.example.isyara.data.Sentence
import com.example.isyara.databinding.FragmentDictionarySentenceBinding


class DictionarySentenceFragment : Fragment() {
    private var _binding: FragmentDictionarySentenceBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionarySentenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.includeToolbar.toolbar)
        activity.supportActionBar?.title = getString(R.string.kata)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val sentenceList = listOf(
            Sentence("Kata1", R.drawable.quiz_img),
            Sentence("Kata2", R.drawable.quiz_img),
            Sentence("Kata3", R.drawable.quiz_img)
        )

        val adapter = DictionarySentenceAdapter(sentenceList)
        binding.rvSentence.layoutManager = GridLayoutManager(context, 2)
        binding.rvSentence.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}