package com.example.isyara.ui.information_event

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
import com.example.isyara.databinding.FragmentEventInformationBinding

class EventInformationFragment : Fragment() {

    private var _binding: FragmentEventInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventInformationViewModel by viewModels {
        EventInformationViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventInformationBinding.inflate(inflater, container, false)

        val pref = UserPreferences(requireContext())
        val token = pref.getToken()

        if (token == null) {
            findNavController().navigate(R.id.action_eventInformationFragment_to_loginFragment)
        } else {
            viewModel.fetchEvent(token)

            setupRecyclerView()
            setupObservers()

            binding.tvCommunity.setOnClickListener {
                findNavController().navigateUp()
            }

            binding.btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            binding.tvBackText.setOnClickListener {
                findNavController().navigateUp()
            }
        }


        return binding.root
    }

    private fun setupObservers() {
        viewModel.event.observe(viewLifecycleOwner, { eventList ->
            val adapter = EventInformationAdapter(eventList) { item ->
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
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
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