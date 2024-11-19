package com.example.isyara.ui.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isyara.R
import com.example.isyara.data.InformationSample
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentHomeScreenBinding

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    private fun getDummyInformationList(): List<InformationSample> {
        return listOf(
            InformationSample("Title 1", "Description 1"),
            InformationSample("Title 2", "Description 2"),
            InformationSample("Title 3", "Description 3"),
            InformationSample("Title 4", "Description 4")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)

        val userPreferences = UserPreferences(requireContext())
        val token = userPreferences.getToken()

//        if (token!!.isEmpty()) {
//            findNavController().navigate(R.id.action_homeScreenFragment_to_loginscreen)
//        }

        // Setup navigasi untuk tiap CardView
        binding.cardView1.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_translateFragment)
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
        // Setup RecyclerView
        val informationList = getDummyInformationList()
        val adapter = HomeScreenAdapter(informationList)
        binding.recyclerViewInformation.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewInformation.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
