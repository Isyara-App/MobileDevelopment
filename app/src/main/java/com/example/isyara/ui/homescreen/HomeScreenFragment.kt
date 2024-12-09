package com.example.isyara.ui.homescreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.example.isyara.util.LoadImage

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
        val name = userPreferences.getName()
        val image = userPreferences.getImage()

        if (image != null) {
            LoadImage.load(
                context = requireContext(),
                imageView = binding.profileImage,
                imageUrl = image!!,
                placeholder = R.color.placeholder,
                isCircle = true
            )
        }

        binding.userName.text = name

        setupObservers()
        setupRecyclerView()

        userPreferences.getToken()?.let {
            viewModel.fetchAllNews(it)
        } ?: {
            Toast.makeText(requireContext(), "Token is null", Toast.LENGTH_SHORT).show()
            userPreferences.clearToken()
            findNavController().navigate(R.id.action_homeScreenFragment_to_loginFragment)
        }

        binding.cardView1.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Izin diberikan, navigasi ke TranslateFragment
//                    val intent = Intent(requireContext(), TranslateActivity::class.java)
//                    startActivity(intent)
                animateCardView(it, R.id.action_homeScreenFragment_to_translateFragment)
            } else {
                // Minta izin kamera
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

        }

        binding.settingsButton.setOnClickListener {
            animateCardView(it, R.id.action_homeScreenFragment_to_settingsFragment)
        }

        binding.cardView2.setOnClickListener {
            animateCardView(it, R.id.action_homeScreenFragment_to_dictionaryFragment)
        }
        binding.cardView3.setOnClickListener {
            animateCardView(it, R.id.action_homeScreenFragment_to_informationFragment)
        }
        binding.cardView4.setOnClickListener {
            animateCardView(it, R.id.action_homeScreenFragment_to_quizFragment)
        }

        return binding.root
    }

    private fun animateCardView(view: View, navigateTo: Int, permissionRequired: String? = null) {
        val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)

        view.startAnimation(scaleUp)
        scaleUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                view.startAnimation(scaleDown)
                scaleDown.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        if (permissionRequired != null) {
                            // Cek jika ada izin yang diperlukan
                            if (ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    permissionRequired
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                findNavController().navigate(navigateTo)
                            } else {
                                requestPermissionLauncher.launch(permissionRequired)
                            }
                        } else {
                            // Navigasi langsung jika tidak memerlukan izin
                            findNavController().navigate(navigateTo)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
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
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                val userPreferences = UserPreferences(requireContext())
                if (errorMessage.isNotEmpty()) {
                    userPreferences.clearToken()
                    findNavController().navigate(R.id.action_homeScreenFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }

            }
        })

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
