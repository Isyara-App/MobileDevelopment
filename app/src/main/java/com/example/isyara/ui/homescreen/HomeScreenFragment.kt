package com.example.isyara.ui.homescreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentHomeScreenBinding

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeScreenViewModel by viewModels {
        HomeScreenViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)

        val userPreferences = UserPreferences(requireContext())
        val token = userPreferences.getToken()

        if (token.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_homeScreenFragment_to_loginFragment)
        }

        setupRecyclerView()
        setupObservers()

        viewModel.fetchAllNews(token!!)

        binding.cardView1.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Izin diberikan, navigasi ke TranslateFragment
                findNavController().navigate(R.id.action_homeScreenFragment_to_translateFragment)
            } else {
                // Minta izin kamera
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        binding.cardView2.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_dictionaryFragment)
        }
        binding.cardView3.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_informationFragment)
        }
        binding.cardView4.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_quizFragment)
        }

        return binding.root
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG)
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG)
            }
        }


    private fun setupRecyclerView() {
        binding.recyclerViewInformation.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewModel.news.observe(viewLifecycleOwner, Observer { informationList ->
            val adapter = HomeScreenAdapter(informationList) { item ->
                val bundle = Bundle().apply {
                    putString("itemId", item.id.toString())
                    putString("itemTitle", item.title)
                    putString("itemDescription", item.description)
                    putString("itemImageUrl", item.imageUrl)
                }
                findNavController().navigate(
                    R.id.action_homeScreenFragment_to_newsDetailFragment,
                    bundle
                )
            }
            binding.recyclerViewInformation.adapter = adapter
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
