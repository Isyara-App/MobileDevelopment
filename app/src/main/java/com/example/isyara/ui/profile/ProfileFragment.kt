package com.example.isyara.ui.profile

import android.net.Uri
import android.os.Build
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
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentProfileBinding
import com.example.isyara.util.LoadImage
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory.getInstance(requireContext())
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

        if (name != null) {
            binding.nameInput.setText(name)
        }

        if (image != null) {
            LoadImage.load(
                context = requireContext(),
                imageView = binding.imgProfile,
                imageUrl = image!!,
                placeholder = R.color.placeholder,
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editProfileButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Memeriksa apakah izin sudah diberikan
                if (requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    startGallery()  // Lanjutkan jika izin sudah diberikan
                } else {
                    // Jika izin belum diberikan, minta izin
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // Jika versi Android lebih rendah dari Marshmallow (API 23), langsung buka galeri
                startGallery()
            }
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
                    // Use content resolver to get actual file path
                    val contentResolver = requireContext().contentResolver
                    val inputStream = contentResolver.openInputStream(uri)

                    // Create a temporary file
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
            val id = userPreferences.getId()
            Log.d("ProfileFragment", "id $id , token $token")

            if (id != null && token != null) {
                Log.d(
                    "ProfileFragment",
                    "Attempting update: token=$token, id=$id, name=$name, imageFile=$imageFile"
                )
                viewModel.update(token, id.toInt(), imageFile, name)
                findNavController().popBackStack()
            } else {
                Log.e("ProfileFragment", "Missing token or ID")
                Toast.makeText(context, "Unable to update profile", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
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
            // Tampilkan pesan error jika ada
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }

        // Observasi perubahan data profil
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            val userPreferences = UserPreferences(requireContext())
            // Perbarui UI dengan profil baru
            LoadImage.load(
                context = requireContext(),
                imageView = binding.imgProfile,
                imageUrl = profile.imageUrl
                    ?: userPreferences.getImage()!!, // Perbarui URL gambar profil
                placeholder = R.color.placeholder,
                isCircle = true
            )
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Izin diberikan, buka galeri
                startGallery()
            } else {
                // Izin ditolak
                Log.d("Permission", "Permission denied")
            }
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