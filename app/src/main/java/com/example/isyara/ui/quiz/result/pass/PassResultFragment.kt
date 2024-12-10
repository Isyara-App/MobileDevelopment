package com.example.isyara.ui.quiz.result.pass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.databinding.FragmentPassResultBinding

class PassResultFragment : Fragment() {
    private var _binding: FragmentPassResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassResultBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_passResultFragment_to_quizFragment,
                null,
                // Pop back stack to remove PassResultFragment from the navigation stack
                NavOptions.Builder()
                    .setPopUpTo(R.id.quizFragment, true) // Pop PassResultFragment
                    .build()
            )
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}