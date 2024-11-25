package com.example.isyara.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)

        // Inisialisasi tombol back
        binding.openWord.setOnClickListener {
            // Navigasi kembali ke HomeScreenFragment
            findNavController().navigate(R.id.action_dictionaryFragment_to_dictionaryWordFragment)
        }
        binding.openSentence.setOnClickListener {
            findNavController().navigate(R.id.action_dictionaryFragment_to_dictionarySentenceFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}