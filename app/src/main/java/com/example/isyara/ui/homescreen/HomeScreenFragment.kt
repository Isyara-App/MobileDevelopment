package com.example.isyara.ui.homescreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isyara.R
import com.example.isyara.data.InformationSample
import com.example.isyara.databinding.FragmentHomeScreenBinding

class HomeScreenFragment : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)

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

        val informationList = InformationSample.getDummyData()
        val adapter = HomeScreenAdapter(informationList) { item ->
            val bundle = Bundle().apply {
                putString("itemId", item.id)  // Mengirimkan ID bertipe String
                putString("itemTitle", item.title)  // Mengirimkan title
                putString("itemDescription", item.description)  // Mengirimkan ID bertipe String
            }
            findNavController().navigate(R.id.action_homeScreenFragment_to_newsDetailFragment, bundle)
        }

        binding.recyclerViewInformation.adapter = adapter
        binding.recyclerViewInformation.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
