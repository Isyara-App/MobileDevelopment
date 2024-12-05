package com.example.isyara.ui.information

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
import com.example.isyara.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InformationViewModel by viewModels {
        InformationViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val userPreferences = UserPreferences(requireContext())
        val token = userPreferences.getToken()

        setupRecyclerView()
        setupObservers()

        userPreferences.getToken()?.let {
            viewModel.fetchCommunity(it)
        } ?: {
            Toast.makeText(requireContext(), "Token is null", Toast.LENGTH_SHORT).show()
            userPreferences.clearToken()
            findNavController().navigate(R.id.action_informationFragment_to_loginFragment)
        }

        binding.tvEvent.setOnClickListener {
            findNavController().navigate(R.id.action_informationFragment_to_eventInformationFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvBackText.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }

    private fun setupObservers() {
        viewModel.community.observe(viewLifecycleOwner, { communityList ->
            val adapter = InformationAdapter(communityList) { item ->
                val bundle = Bundle().apply {
                    putString("itemId", item.id.toString())
                    putString("itemTitle", item.title)
                    putString("itemDescription", item.description)
                    putString("itemImageUrl", item.imageUrl)
                }
            }
            binding.recyclerViewCommunity.adapter = adapter
        })

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
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCommunity.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}