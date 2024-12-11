package com.example.isyara.ui.dictionary_word

import DictionaryWordAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentDictionaryWordBinding


class DictionaryWordFragment : Fragment() {

    private val dictionaryWordViewModel: DictionaryWordViewModel by viewModels {
        DictionaryWordViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentDictionaryWordBinding? = null
    private val binding get() = _binding!!
    private val debounceTime = 500L

    private lateinit var adapter: DictionaryWordAdapter

    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupObserver()

        // Get token
        val userPreferences = UserPreferences(requireContext())
        token = userPreferences.getToken() ?: ""

        // Fetch all data initially
        dictionaryWordViewModel.searchSentence(token, "")

        // Tambahkan TextWatcher untuk melakukan pencarian saat user mengetik
        binding.tfSearch.editText?.addTextChangedListener(object : TextWatcher {
            private val handler = Handler(Looper.getMainLooper())
            private var runnable: Runnable? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Hapus callback sebelumnya untuk mencegah pencarian berulang
                runnable?.let { handler.removeCallbacks(it) }

                // Buat runnable baru dengan debounce
                runnable = Runnable {
                    val query = s.toString()
                    dictionaryWordViewModel.searchSentence(token, query)
                }

                // Jalankan pencarian dengan delay
                handler.postDelayed(runnable!!, debounceTime)
            }
        })


        // Perform search when user inputs a query
        binding.tfSearch.editText?.setOnEditorActionListener { _, _, _ ->
            val query = binding.tfSearch.editText?.text.toString()
            dictionaryWordViewModel.searchSentence(token, query)
            true
        }
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.includeToolbar.toolbar)
        activity.supportActionBar?.title = getString(R.string.huruf)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = DictionaryWordAdapter(emptyList())
        binding.rvWord.layoutManager = GridLayoutManager(context, 2)
        binding.rvWord.adapter = adapter
    }

    private fun setupObserver() {
        dictionaryWordViewModel.sentences.observe(viewLifecycleOwner) { sentences ->
            if (sentences.isNotEmpty()) {
                adapter = DictionaryWordAdapter(sentences)
                binding.rvWord.adapter = adapter
                binding.rvWord.isVisible = true
                binding.imgEmpty.isVisible = false
            } else {
                binding.rvWord.isVisible = false
                binding.imgEmpty.isVisible = true
            }
        }

        dictionaryWordViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        dictionaryWordViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.imgEmpty.isVisible = true
            binding.rvWord.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}