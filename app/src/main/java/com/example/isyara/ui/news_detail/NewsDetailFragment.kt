package com.example.isyara.ui.news_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.isyara.R
import com.example.isyara.databinding.FragmentNewsDetailBinding
import com.example.isyara.util.LoadImage

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
        val imageUrl =
            arguments?.getString("itemImageUrl") // Pastikan URL gambar dikirim melalui Bundle

        // Menampilkan data pada TextView yang sesuai
        binding.tvTitle.text = itemTitle
        val formattedDescription = itemDescription?.replace(". ", ".\n\n")
        binding.tvContent.text = formattedDescription

        // Memuat gambar dengan util LoadImage
        imageUrl?.let {
            LoadImage.load(
                context = requireContext(),
                imageView = binding.imgCover,
                imageUrl = it,
                placeholder = R.color.placeholder
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvBackText.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
