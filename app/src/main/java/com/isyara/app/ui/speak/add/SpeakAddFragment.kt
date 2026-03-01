package com.isyara.app.ui.speak.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.isyara.app.databinding.FragmentSpeakAddBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class SpeakAddFragment : Fragment() {

    private var _binding: FragmentSpeakAddBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val speakAddViewModel: SpeakAddViewModel by viewModels {
        SpeakAddViewModelFactory.getInstance(requireContext())
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivPreview.setImageURI(uri)
            binding.tvUploadPrompt.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeakAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cvImagePicker.setOnClickListener {
            startGallery()
        }

        binding.btnSave.setOnClickListener {
            val targetText = binding.etTargetText.text.toString().trim()
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (targetText.isEmpty()) {
                Toast.makeText(requireContext(), "Teks target tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val internalUri = saveImageToInternalStorage(selectedImageUri!!)
            if (internalUri != null) {
                speakAddViewModel.savePracticeItem(internalUri.toString(), targetText)
            } else {
                Toast.makeText(requireContext(), "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }

        setupObservers()
    }

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val fileName = "practice_img_${UUID.randomUUID()}.jpg"
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream?.copyTo(outputStream)
            
            inputStream?.close()
            outputStream.close()
            
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun startGallery() {
        launcherIntentGallery.launch("image/*")
    }

    private fun setupObservers() {
        speakAddViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }

        speakAddViewModel.isSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                Toast.makeText(requireContext(), "Latihan berhasil disimpan", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Gagal menyimpan latihan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
