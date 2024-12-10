package com.example.isyara.ui.quiz.ingame

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
import com.example.isyara.databinding.FragmentInGameBinding
import com.example.isyara.util.LoadImage

class InGameFragment : Fragment() {

    private var _binding: FragmentInGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InGameViewModel by viewModels {
        InGameViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInGameBinding.inflate(inflater, container, false)
        val itemId = arguments?.getString("itemId")
        val userPreferences = UserPreferences(requireContext())

        observeViewModel()

        userPreferences.getToken()?.let {
            viewModel.fetchLevelById(it, itemId!!.toInt())
        } ?: {
            Toast.makeText(requireContext(), "Token is null", Toast.LENGTH_SHORT).show()
            userPreferences.clearToken()
            findNavController().navigate(R.id.action_quizFragment_to_onboardFragment)
        }


        binding.startButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("itemId", itemId!!.toInt())
            findNavController().navigate(R.id.action_inGameFragment_to_questionFragment, bundle)

        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.levelData.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.tvLevel.text = it.title
                binding.levelTitle.text = it.name
                binding.levelDescription.text = it.description
                LoadImage.load(
                    context = requireContext(),
                    imageView = binding.levelImage,
                    imageUrl = it.imageUrl!!,
                    placeholder = R.color.placeholder
                )
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Menampilkan atau menyembunyikan ProgressBar berdasarkan status loading
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.levelContainer.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.levelContainer.visibility =
                    View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.d("ErrorMessage", it) // Menampilkan error di log
                if (it == "An internal server error occurred" || it == "You must complete level 1 before accessing this level.") {
                    findNavController().popBackStack()
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}