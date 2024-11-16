package com.example.isyara.ui.dictionary_word

import DictionaryWordAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isyara.R
import com.example.isyara.data.Word
import com.example.isyara.databinding.FragmentDictionaryWordBinding


class DictionaryWordFragment : Fragment() {

    private var _binding: FragmentDictionaryWordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.includeToolbar.toolbar)
        activity.supportActionBar?.title = getString(R.string.huruf)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val wordList = listOf(
            Word("A", R.drawable.quiz_img),
            Word("B", R.drawable.quiz_img),
            Word("C", R.drawable.quiz_img)
        )

        val adapter = DictionaryWordAdapter(wordList)
        binding.rvWord.layoutManager = GridLayoutManager(context, 2)
        binding.rvWord.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
