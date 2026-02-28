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
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentHomeScreenBinding
import com.example.isyara.util.LoadImage

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)

        val userPreferences = UserPreferences(requireContext())
        val name = userPreferences.getName()
        val image = userPreferences.getImage()

        if (!image.isNullOrEmpty()) {
            LoadImage.load(
                context = requireContext(),
                imageView = binding.profileImage,
                imageUrl = image!!,
                placeholder = R.color.placeholder,
                isCircle = true
            )
        }

        binding.userName.text = name

        binding.cardView1.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                animateCardView(it, R.id.action_homeScreenFragment_to_translateFragment)
            } else {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
