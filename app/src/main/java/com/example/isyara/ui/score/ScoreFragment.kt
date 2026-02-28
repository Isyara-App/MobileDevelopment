package com.example.isyara.ui.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentScoreBinding

class ScoreFragment : Fragment() {

    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    private val scoreViewModel: ScoreViewModel by viewModels {
        ScoreViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: ScoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        val userPreferences = UserPreferences(requireContext())
        val token = userPreferences.getToken() ?: ""
        scoreViewModel.fetchScores(token)
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvBackText.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = ScoreAdapter(emptyList())
        binding.rvScores.layoutManager = LinearLayoutManager(requireContext())
        binding.rvScores.adapter = adapter
    }

    private fun setupObservers() {
        scoreViewModel.scores.observe(viewLifecycleOwner) { scores ->
            if (scores.isNotEmpty()) {
                adapter = ScoreAdapter(scores)
                binding.rvScores.adapter = adapter
                binding.rvScores.isVisible = true
                binding.tvNoScores.isVisible = false
            } else {
                binding.rvScores.isVisible = false
                binding.tvNoScores.isVisible = true
            }
        }

        scoreViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        scoreViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.tvNoScores.isVisible = true
                binding.rvScores.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
