package com.isyara.app.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isyara.app.R
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val userPreferences = UserPreferences(requireContext())

        setupRecyclerView()
        setupObservers()

        userPreferences.getToken()?.let {
            viewModel.fetchLevels(it)
        } ?: run {
            Toast.makeText(requireContext(), "Token is null", Toast.LENGTH_SHORT).show()
            userPreferences.clearToken()
            findNavController().navigate(R.id.action_quizFragment_to_onboardFragment)
        }


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (it.contains("Unauthenticated", ignoreCase = true) ||
                    it.contains("401", ignoreCase = true)) {
                    val userPreferences = UserPreferences(requireContext())
                    userPreferences.clearToken()
                    findNavController().navigate(R.id.action_quizFragment_to_onboardFragment)
                } else if (it.isNotEmpty()) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }

        viewModel.level.observe(viewLifecycleOwner) { levelList ->
            val adapter = QuizAdapter(levelList) { item ->
                val bundle = Bundle().apply {
                    putString("itemId", item.id.toString())
                    
                }

                findNavController().navigate(R.id.action_quizFragment_to_inGameFragment, bundle)
            }
            binding.recyclerViewQuestion.adapter = adapter
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewQuestion.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}