package com.example.isyara.ui.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentOnboardBinding

class OnboardFragment : Fragment() {

    private var _binding: FragmentOnboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardBinding.inflate(inflater, container, false)
        val view = binding.root

        val userPreferences = UserPreferences(requireContext())
        val token = userPreferences.getToken()

        if (token != null) {
            findNavController().navigate(R.id.action_onboardFragment_to_homeScreenFragment)
        }
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardFragment_to_loginFragment)
        }
        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_onboardFragment_to_signupFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}