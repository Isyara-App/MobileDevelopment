package com.example.isyara.ui.dictionary

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)

        binding.openWordButton.setOnClickListener {
            animateCardView(it,R.id.action_dictionaryFragment_to_dictionaryWordFragment)
        }
        binding.openSentenceButton.setOnClickListener {
            animateCardView(it,R.id.action_dictionaryFragment_to_dictionarySentenceFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}