package com.isyara.app.ui.loginandsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.isyara.app.R
import com.isyara.app.data.Result
import com.isyara.app.databinding.FragmentSignupBinding

class SignUpFragment : Fragment() {

    private val signupViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        setupObservers()
        setupListeners()
        return binding.root
    }

    private fun setupObservers() {
        signupViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.signupButton.isEnabled = !isLoading
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.signupButton.text = if (isLoading) "" else getString(R.string.signup_text)
        }

        signupViewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                Toast.makeText(requireContext(), "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            } else if (result is Result.Error) {
                showError(result.error)
            }
        }

        signupViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                signupViewModel.register(name, email, password, confirmPassword)
            }
        }

        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            name.isBlank() -> {
                binding.nameInput.error = getString(R.string.error_name_required)
                false
            }

            email.isBlank() -> {
                binding.emailInput.error = getString(R.string.error_email_required)
                false
            }

            password.isBlank() -> {
                binding.passwordInput.error = getString(R.string.error_password_required)
                false
            }

            password.length < 8 -> {
                binding.passwordInput.error = "Password minimal 8 karakter"
                false
            }

            confirmPassword.isBlank() -> {
                binding.confirmPasswordInput.error = "Konfirmasi password harus diisi"
                false
            }

            password != confirmPassword -> {
                binding.confirmPasswordInput.error = "Password tidak cocok"
                false
            }

            else -> true
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
