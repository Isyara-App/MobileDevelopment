package com.isyara.app.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.isyara.app.R
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.databinding.FragmentProfileBinding
import com.isyara.app.util.LoadImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        com.isyara.app.ui.profile.ProfileViewModelFactory.getInstance(requireContext())
    }

    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val userPreferences = UserPreferences(requireContext())
        val name = userPreferences.getName()
        val image = userPreferences.getImage()

        observeViewModel()

        // Load from local preferences first
        if (name != null) {
            binding.nameInput.setText(name)
        }

        if (image != null) {
            LoadImage.load(
                context = requireContext(),
                imageView = binding.imgProfile,
                imageUrl = image,
                placeholder = R.color.placeholder,
            )
        }

        // Fetch from server to get latest data
        userPreferences.getToken()?.let {
            viewModel.fetchProfile(it)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editProfileButton.setOnClickListener {
            startGallery()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.openWordButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            if (name.isEmpty()) {
                binding.nameInput.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            val imageFile = currentImageUri?.let { uri ->
                try {
                    val contentResolver = requireContext().contentResolver
                    val inputStream = contentResolver.openInputStream(uri)

                    val tempFile = File(requireContext().cacheDir, "profile_image.jpg")
                    tempFile.createNewFile()
                    tempFile.outputStream().use { fileOut ->
                        inputStream?.copyTo(fileOut)
                    }
                    tempFile
                } catch (e: Exception) {
                    Log.e("ProfileFragment", "Error creating file: ${e.message}")
                    null
                }
            }

            val token = userPreferences.getToken()

            if (token != null) {
                Log.d(
                    "ProfileFragment",
                    "Attempting update: token=$token, name=$name, imageFile=$imageFile"
                )
                viewModel.update(token, imageFile, name)
            } else {
                Log.e("ProfileFragment", "Missing token")
                Toast.makeText(context, "Unable to update profile", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteAccountButton.setOnClickListener {
            showDeleteAccountConfirmation(userPreferences)
        }

        return binding.root
    }

    private fun showDeleteAccountConfirmation(userPreferences: UserPreferences) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage("Apakah Anda yakin ingin menghapus akun? Tindakan ini tidak dapat dibatalkan.")
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Hapus") { _, _ ->
                val token = userPreferences.getToken()
                if (token != null) {
                    viewModel.deleteAccount(token)
                } else {
                    Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.openWordButton.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.openWordButton.visibility = View.VISIBLE
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Log.d("ProfileFragment", "Error message: $error")
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.photo.observe(viewLifecycleOwner) { image ->
            LoadImage.load(
                context = requireContext(),
                imageView = binding.imgProfile,
                imageUrl = image,
                placeholder = R.color.placeholder,
            )
        }

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            val userPreferences = UserPreferences(requireContext())
            binding.nameInput.setText(profile.name ?: userPreferences.getName() ?: "")
            LoadImage.load(
                context = requireContext(),
                imageView = binding.imgProfile,
                imageUrl = profile.imageUrl ?: userPreferences.getImage()
                    ?: "ic_profile",
                placeholder = R.color.placeholder,
            )
        }

        viewModel.deleteAccountResult.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                Toast.makeText(context, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_profileFragment_to_onboardFragment)
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imgProfile.setImageURI(it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}