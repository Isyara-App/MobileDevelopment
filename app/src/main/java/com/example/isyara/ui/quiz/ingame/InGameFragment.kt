package com.example.isyara.ui.quiz.ingame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.databinding.FragmentInGameBinding

class InGameFragment : Fragment() {

    private var _binding: FragmentInGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInGameBinding.inflate(inflater, container, false)
        val itemId = arguments?.getString("itemId")
        val itemTitle = arguments?.getString("itemTitle")
        val itemName = arguments?.getString("itemName")
        val itemDescription = arguments?.getString("itemDescription")

        binding.tvLevel.text = itemTitle
        binding.levelTitle.text = itemName
        binding.levelDescription.text = itemDescription


        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_dictionaryFragment_to_dictionaryWordFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}