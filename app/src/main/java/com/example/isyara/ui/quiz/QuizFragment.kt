package com.example.isyara.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentQuizBinding

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
        } ?: {
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
                val userPreferences = UserPreferences(requireContext())
                if (errorMessage.isNotEmpty()) {
                    userPreferences.clearToken()
                    findNavController().navigate(R.id.action_informationFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }

            }
        }

        viewModel.level.observe(viewLifecycleOwner) { levelList ->
            val adapter = QuizAdapter(levelList) { item ->
                val bundle = Bundle().apply {
                    putString("itemId", item.id.toString())
                    putString("itemTitle", item.title)
                    putString("itemName", item.name)
                    putString("itemDescription", item.description)

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