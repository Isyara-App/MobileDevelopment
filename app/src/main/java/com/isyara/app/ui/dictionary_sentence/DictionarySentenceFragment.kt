package com.isyara.app.ui.dictionary_sentence

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
import com.isyara.app.databinding.FragmentDictionarySentenceBinding

class DictionarySentenceFragment : Fragment() {

    private val dictionarySentenceViewModel: DictionarySentenceViewModel by viewModels {
        DictionarySentenceViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentDictionarySentenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DictionarySentenceAdapter

    private lateinit var token: String
    private val debounceTime = 500L
    private var currentFilter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionarySentenceBinding.inflate(inflater, container, false)
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
        dictionarySentenceViewModel.searchSentence(token, "", currentFilter)

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
                    dictionarySentenceViewModel.searchSentence(token, query, currentFilter)
                }

                // Jalankan pencarian dengan delay
                handler.postDelayed(runnable!!, debounceTime)
            }
        })

        // Optional: tetap pertahankan pencarian saat tombol Enter ditekan
        binding.tfSearch.editText?.setOnEditorActionListener { _, _, _ ->
            val query = binding.tfSearch.editText?.text.toString()
            dictionarySentenceViewModel.searchSentence(token, query, currentFilter)
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
            dictionarySentenceViewModel.searchSentence(token, query, currentFilter)
        }
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.includeToolbar.toolbar)
        activity.supportActionBar?.title = getString(R.string.kata)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.includeToolbar.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = DictionarySentenceAdapter(emptyList()) { item, isKnowing ->
            item.id?.let { id ->
                dictionarySentenceViewModel.toggleLearningStatus(token, id, isKnowing)
            }
        }
        binding.rvSentence.layoutManager = GridLayoutManager(context, 2)
        binding.rvSentence.adapter = adapter
    }

    private fun setupObserver() {
        dictionarySentenceViewModel.sentences.observe(viewLifecycleOwner) { sentences ->
            if (sentences.isNotEmpty()) {
                adapter = DictionarySentenceAdapter(sentences) { item, isKnowing ->
                    item.id?.let { id ->
                        dictionarySentenceViewModel.toggleLearningStatus(token, id, isKnowing)
                    }
                }
                binding.rvSentence.adapter = adapter
                binding.rvSentence.isVisible = true
                binding.imgEmpty.isVisible = false
            } else {
                binding.rvSentence.isVisible = false
                binding.imgEmpty.isVisible = true
            }
        }

        dictionarySentenceViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        dictionarySentenceViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.imgEmpty.isVisible = true
            binding.rvSentence.isVisible = false
        }

        dictionarySentenceViewModel.toggleResult.observe(viewLifecycleOwner) { success ->
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
