package com.example.isyara.ui.help

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HelpViewModel by viewModels {
        HelpViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)

        val userPreferences: UserPreferences = UserPreferences(requireContext())
        observeViewModel()

        // Inisialisasi tombol back
        binding.backButton.setOnClickListener {
            // Navigasi kembali ke HomeScreenFragment
            findNavController().navigateUp()
        }

        binding.sendButton.setOnClickListener {
            userPreferences.getToken()?.let {
                viewModel.sendMessage(
                    it,
                    binding.nameInput.text.toString(),
                    binding.emailInput.text.toString(),
                    binding.messageInput.text.toString(),
                )
            } ?: {
                Toast.makeText(requireContext(), "Token is null", Toast.LENGTH_SHORT).show()
                userPreferences.clearToken()
                findNavController().navigate(R.id.action_helpFragment_to_onboardFragment)
            }

            viewModel.message.observe(viewLifecycleOwner) { message ->
                Log.d("HelpFragment", "Received message: $message")


                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()


            }
            Toast.makeText(requireContext(), "pesan telah dikirim", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()

        }

        return binding.root
    }

    private fun observeViewModel() {


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.sendButton.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.sendButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}