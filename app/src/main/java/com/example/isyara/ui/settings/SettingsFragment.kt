package com.example.isyara.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val userPreferences = UserPreferences(requireContext())

        // setupObservers()


        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        binding.helpButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_helpFragment)
        }

        binding.logoutButton.setOnClickListener {
            Toast.makeText(requireContext(), "Logout Success", Toast.LENGTH_SHORT).show()
            userPreferences.clearToken()
            findNavController().navigate(R.id.action_settingsFragment_to_onboardFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

//    private fun setupObservers() {
//        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
//            errorMessage?.let {
//                val userPreferences = UserPreferences(requireContext())
//                if (errorMessage.isNotEmpty()) {
//                    userPreferences.clearToken()
//                    findNavController().navigate(R.id.action_settingsFragment_to_onboardFragment)
//                } else {
//                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//
//                }
//            }
//        }
//
//        viewModel.logoutResult.observe(viewLifecycleOwner) { result ->
//            Toast.makeText(requireContext(), "Logout Success", Toast.LENGTH_SHORT).show()
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}