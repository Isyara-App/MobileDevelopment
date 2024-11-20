package com.example.isyara.ui.news_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.isyara.R
import com.example.isyara.databinding.FragmentNewsDetailBinding

class NewsDetailFragment : Fragment(R.layout.fragment_news_detail) {

    private var _binding: FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)

        // Mengambil data dari Bundle
        val itemId = arguments?.getString("itemId")
        val itemTitle = arguments?.getString("itemTitle")
        val itemDescription = arguments?.getString("itemDescription")

        // Menampilkan data pada TextView yang sesuai
        binding.tvTitle.text = itemTitle
        binding.tvContent.text = itemDescription

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

