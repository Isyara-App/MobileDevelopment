package com.isyara.app.ui.speak.practice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.isyara.app.R
import com.isyara.app.data.local.room.SpeakDatabase
import com.isyara.app.databinding.FragmentSpeakPracticeBinding
import com.isyara.app.util.FuzzyMatchUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.max

class SpeakPracticeFragment : Fragment() {

    private var _binding: FragmentSpeakPracticeBinding? = null
    private val binding get() = _binding!!

    private var practiceItemId: Int = -1
    private var targetText: String = ""

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var attemptCount = 0
    private val recognitionLocale = Locale("id", "ID")
    private var partialMatches = emptyList<String>()
    private var maxRmsDb = Float.NEGATIVE_INFINITY
    private var detectedSpeechInSession = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupSpeechRecognizer()
        } else {
            Toast.makeText(requireContext(), "Akses mikrofon dibutuhkan untuk fitur ini!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            practiceItemId = it.getInt("itemId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeakPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        loadPracticeItem()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED) {
            setupSpeechRecognizer()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        // Click-to-toggle approach: single tap starts, single tap stops
        // Much more reliable than hold-to-talk for users with limited motor control
        binding.fabMic.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
        }
    }

    private fun loadPracticeItem() {
        if (practiceItemId == -1) return

        lifecycleScope.launch(Dispatchers.IO) {
            val db = SpeakDatabase.getDatabase(requireContext())
            val item = db.practiceItemDao().getPracticeItemById(practiceItemId)

            withContext(Dispatchers.Main) {
                item?.let {
                    targetText = it.targetText
                    binding.tvTargetText.text = targetText
                    try {
                        com.isyara.app.util.LoadImage.load(
                            context = requireContext(),
                            imageView = binding.ivPracticeImage,
                            imageUrl = it.imageUri,
                            placeholder = android.R.color.darker_gray
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Speech Recognition tidak tersedia di HP ini", Toast.LENGTH_LONG).show()
            binding.fabMic.isEnabled = false
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                if (_binding == null) return
                resetRecognitionSession()
                binding.tvStatus.text = "🎙️ Mendengarkan... Bicara sekarang!"
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            }

            override fun onBeginningOfSpeech() {
                detectedSpeechInSession = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Visual feedback: pulse the mic button based on voice volume
                if (_binding == null) return
                maxRmsDb = max(maxRmsDb, rmsdB)
                val scale = 1.0f + (rmsdB.coerceIn(0f, 10f) / 50f)
                binding.fabMic.scaleX = scale
                binding.fabMic.scaleY = scale
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                if (_binding == null) return
                binding.tvStatus.text = "⏳ Memproses ucapanmu..."
                binding.fabMic.scaleX = 1.0f
                binding.fabMic.scaleY = 1.0f
                setMicInactive()
            }

            override fun onError(error: Int) {
                isListening = false
                if (_binding == null) return
                setMicInactive()
                binding.fabMic.scaleX = 1.0f
                binding.fabMic.scaleY = 1.0f

                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        if (partialMatches.isNotEmpty()) {
                            processResults(partialMatches, usedPartialFallback = true)
                            return
                        }

                        binding.tvStatus.text = if (heardSomethingInSession()) {
                            "Suaramu masuk, tapi masih terlalu pelan untuk dikenali. Coba lebih dekat ke mic ya 😊"
                        } else {
                            "Belum terdengar suaramu. Coba tekan 🎤 lagi dan bicara lebih dekat ya 😊"
                        }
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                    SpeechRecognizer.ERROR_NETWORK,
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        binding.tvStatus.text = "Butuh internet untuk mendengar. Coba periksa koneksimu 📶"
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                    SpeechRecognizer.ERROR_AUDIO -> {
                        binding.tvStatus.text = "Mikrofon bermasalah. Coba tutup app lain yang pakai mikrofon."
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                    else -> {
                        binding.tvStatus.text = "Ada gangguan. Coba tekan 🎤 lagi ya!"
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                if (_binding == null) return
                setMicInactive()

                val finalMatches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).orEmpty()
                val mergedMatches = (finalMatches + partialMatches)
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()

                if (mergedMatches.isNotEmpty()) {
                    processResults(mergedMatches, usedPartialFallback = finalMatches.isEmpty())
                } else {
                    binding.tvStatus.text = if (heardSomethingInSession()) {
                        "Suaramu masih sangat pelan, jadi belum terbaca jelas. Coba lebih dekat ke mic ya 🔊"
                    } else {
                        "Belum terdengar. Coba bicara lebih keras ya 🔊"
                    }
                    binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Show partial text while user is still speaking
                if (_binding == null) return
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    partialMatches = (partialMatches + partial)
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .distinct()
                        .take(10)
                    binding.tvResultText.visibility = View.VISIBLE
                    binding.tvResultText.text = "💬 ${partial[0]}..."
                    binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#888888"))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun startListening() {
        if (speechRecognizer == null) {
            setupSpeechRecognizer()
            if (speechRecognizer == null) return
        }

        resetRecognitionSession()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10) // More results = better chance
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Live feedback
            // Some engines ignore these extras, but when supported they help soft/whisper speech.
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
        }

        try {
            speechRecognizer?.startListening(intent)
            isListening = true
            setMicActive()
            binding.tvStatus.text = "🎙️ Mendengarkan..."
            binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            // Reset result text
            binding.tvResultText.visibility = View.GONE
        } catch (e: Exception) {
            isListening = false
            binding.tvStatus.text = "Gagal memulai. Coba lagi ya!"
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
        }
    }

    private fun stopListening() {
        if (!isListening) return
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Ignore
        }
        isListening = false
        setMicInactive()
    }

    private fun setMicActive() {
        binding.fabMic.backgroundTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor("#F44336") // Red = recording
        )
    }

    private fun setMicInactive() {
        binding.fabMic.backgroundTintList = android.content.res.ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.primary) // Original color
        )
    }

    private fun resetRecognitionSession() {
        partialMatches = emptyList()
        maxRmsDb = Float.NEGATIVE_INFINITY
        detectedSpeechInSession = false
    }

    private fun heardSomethingInSession(): Boolean {
        return detectedSpeechInSession || partialMatches.isNotEmpty() || maxRmsDb >= 1.5f
    }

    private fun processResults(matches: List<String>, usedPartialFallback: Boolean = false) {
        attemptCount++
        var highestScore = 0.0
        var bestMatch = ""

        // Check ALL results for fuzzy matching
        for (match in matches) {
            val score = FuzzyMatchUtils.calculateSimilarity(targetText, match)
            if (score > highestScore) {
                highestScore = score
                bestMatch = match
            }
        }

        binding.tvResultText.visibility = View.VISIBLE
        binding.tvResultText.text = if (usedPartialFallback) {
            "Tangkapan sementara: \"$bestMatch\""
        } else {
            "Kamu bilang: \"$bestMatch\""
        }

        // Threshold: 40% match (very forgiving for deaf users)
        when {
            highestScore >= 0.4 -> {
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.text = if (usedPartialFallback) {
                    "🌟 BENAR! Aku tetap bisa menangkap suaramu yang pelan 🌟"
                } else {
                    "🌟 BENAR! HEBAT SEKALI! 🌟"
                }
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.textSize = 18f
            }
            attemptCount >= 3 -> {
                // After 3 attempts, be very encouraging and lower the bar
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.text = "👏 Bagus sekali sudah mencoba ${attemptCount}x! Kamu hebat! 👏"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.textSize = 16f
            }
            else -> {
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.text = "Hampir benar! Coba lagi pelan-pelan ya 😊 (Percobaan ke-$attemptCount)"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.textSize = 14f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        speechRecognizer = null
        _binding = null
    }
}
