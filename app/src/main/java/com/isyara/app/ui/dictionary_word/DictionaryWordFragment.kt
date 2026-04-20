package com.isyara.app.ui.dictionary_word

import DictionaryWordAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.isyara.app.R
import com.isyara.app.data.pref.UserPreferences
import com.isyara.app.databinding.FragmentDictionaryWordBinding


class DictionaryWordFragment : Fragment() {

    private val dictionaryWordViewModel: DictionaryWordViewModel by viewModels {
        DictionaryWordViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentDictionaryWordBinding? = null
    private val binding get() = _binding!!
    private val debounceTime = 500L

    private lateinit var adapter: DictionaryWordAdapter

    private lateinit var token: String
    private var currentFilter: String? = null

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
        setupFilterChips()

        // Get token
        val userPreferences = UserPreferences(requireContext())
        token = userPreferences.getToken() ?: ""

        // Fetch all data initially
        dictionaryWordViewModel.searchSentence(token, "", currentFilter)

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
                    dictionaryWordViewModel.searchSentence(token, query, currentFilter)
                }

                // Jalankan pencarian dengan delay
                handler.postDelayed(runnable!!, debounceTime)
            }
        })


        // Perform search when user inputs a query
        binding.tfSearch.editText?.setOnEditorActionListener { _, _, _ ->
            val query = binding.tfSearch.editText?.text.toString()
            dictionaryWordViewModel.searchSentence(token, query, currentFilter)
            true
        }
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.chipBisindo) -> "1"
                checkedIds.contains(R.id.chipSibi) -> "0"
                else -> null // "Semua"
            }
            val query = binding.tfSearch.editText?.text.toString()
            dictionaryWordViewModel.searchSentence(token, query, currentFilter)
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
        adapter = DictionaryWordAdapter(emptyList()) { item, isKnowing ->
            item.id?.let { id ->
                dictionaryWordViewModel.toggleLearningStatus(token, id, isKnowing)
            }
        }
        binding.rvWord.layoutManager = GridLayoutManager(context, getSpanCount())
        binding.rvWord.adapter = adapter
        binding.rvWord.setHasFixedSize(true)
    }

    private fun getSpanCount(): Int {
        val screenWidthDp = resources.configuration.screenWidthDp
        return when {
            screenWidthDp >= 840 -> 4
            screenWidthDp >= 600 -> 3
            screenWidthDp < 360 -> 1
            else -> 2
        }
    }

    private fun setupObserver() {
        dictionaryWordViewModel.sentences.observe(viewLifecycleOwner) { sentences ->
            if (sentences.isNotEmpty()) {
                adapter = DictionaryWordAdapter(sentences) { item, isKnowing ->
                    item.id?.let { id ->
                        dictionaryWordViewModel.toggleLearningStatus(token, id, isKnowing)
                    }
                }
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

        dictionaryWordViewModel.toggleResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Status belajar diperbarui", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
