package com.isyara.app.ui.quiz.result.failed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.isyara.app.R
import com.isyara.app.databinding.FragmentFailedResultBinding

class FailedResultFragment : Fragment() {
    private var _binding: FragmentFailedResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFailedResultBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_failedResultFragment_to_quizFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.quizFragment, true)
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