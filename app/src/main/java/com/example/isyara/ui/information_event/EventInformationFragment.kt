package com.example.isyara.ui.information_event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.isyara.databinding.FragmentEventInformationBinding

class EventInformationFragment : Fragment() {

    private var _binding: FragmentEventInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventInformationBinding.inflate(inflater, container, false)

        binding.tvCommunity.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvBackText.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}