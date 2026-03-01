package com.isyara.app.ui.speak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isyara.app.R
import com.isyara.app.databinding.FragmentSpeakBinding

class SpeakFragment : Fragment() {

    private var _binding: FragmentSpeakBinding? = null
    private val binding get() = _binding!!

    private val speakViewModel: SpeakViewModel by viewModels {
        SpeakViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: SpeakAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeakBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvTitle.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_speakFragment_to_speakAddFragment)
        }

        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        speakViewModel.fetchPracticeItems()
    }

    private fun setupRecyclerView() {
        adapter = SpeakAdapter(
            onItemClick = { item ->
                val bundle = Bundle().apply {
                    putInt("itemId", item.id)
                }
                findNavController().navigate(R.id.action_speakFragment_to_speakPracticeFragment, bundle)
            },
            onDeleteClick = { item ->
                speakViewModel.deletePracticeItem(item)
            }
        )
        binding.rvPracticeItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPracticeItems.adapter = adapter
    }

    private fun setupObservers() {
        speakViewModel.practiceItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvPracticeItems.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvPracticeItems.visibility = View.VISIBLE
                adapter.submitList(items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
