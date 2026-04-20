package com.isyara.app.ui.speak.practice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
    // Track consecutive low-volume sessions to suggest mic position
    private var lowVolumeStreak = 0
    // Track frames where RMS exceeded speech-level threshold (distinguishes real speech from silence)
    private var speechLevelFrames = 0
    // Track attempts where the child spoke but STT produced zero transcription
    private var detectedButNoTextCount = 0

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
                    attemptCount = 0
                    detectedButNoTextCount = 0
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
                binding.pbVolumeMeter.visibility = View.VISIBLE
                binding.pbVolumeMeter.progress = 0
            }

            override fun onBeginningOfSpeech() {
                detectedSpeechInSession = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (_binding == null) return
                maxRmsDb = max(maxRmsDb, rmsdB)
                // Count frames above speech-level threshold to confirm real vocalization
                if (rmsdB > 2.5f) speechLevelFrames++
                // Pulse mic button
                val scale = 1.0f + (rmsdB.coerceIn(0f, 10f) / 50f)
                binding.fabMic.scaleX = scale
                binding.fabMic.scaleY = scale
                // Volume meter bar: map -2..10 dB -> 0..100
                val progress = ((rmsdB + 2f) / 12f * 100f).toInt().coerceIn(0, 100)
                binding.pbVolumeMeter.progress = progress
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                if (_binding == null) return
                binding.tvStatus.text = "⏳ Memproses ucapanmu..."
                binding.fabMic.scaleX = 1.0f
                binding.fabMic.scaleY = 1.0f
                binding.pbVolumeMeter.visibility = View.INVISIBLE
                setMicInactive()
            }

            override fun onError(error: Int) {
                isListening = false
                if (_binding == null) return
                setMicInactive()
                binding.fabMic.scaleX = 1.0f
                binding.fabMic.scaleY = 1.0f
                binding.pbVolumeMeter.visibility = View.INVISIBLE

                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        if (partialMatches.isNotEmpty()) {
                            processResults(partialMatches, usedPartialFallback = true)
                            return
                        }

                        // Reinitialize recognizer before next attempt (fixes stuck state on some devices)
                        reinitSpeechRecognizer()

                        if (!heardSomethingInSession()) lowVolumeStreak++
                        else lowVolumeStreak = 0

                        binding.tvStatus.text = when {
                            lowVolumeStreak >= 2 ->
                                "Pastikan mikrofon tidak tertutup dan dekatkan HP ke mulut ya 📱"
                            heardSomethingInSession() ->
                                "Suaramu masuk, tapi belum cukup jelas. Coba lebih keras dan pelan-pelan ya 😊"
                            else ->
                                "Belum terdengar suaramu. Tekan 🎤 dan langsung bicara ya 😊"
                        }
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        // Recognizer was busy — reinit and let user retry
                        reinitSpeechRecognizer()
                        binding.tvStatus.text = "Siap lagi! Tekan 🎤 dan coba lagi ya 😊"
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                    SpeechRecognizer.ERROR_NETWORK,
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        binding.tvStatus.text = "Butuh internet untuk mendengar. Coba periksa koneksimu 📶"
                        binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                    }
                    SpeechRecognizer.ERROR_AUDIO -> {
                        reinitSpeechRecognizer()
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
                binding.pbVolumeMeter.visibility = View.INVISIBLE

                val finalMatches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).orEmpty()
                val mergedMatches = (finalMatches + partialMatches)
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()

                if (mergedMatches.isNotEmpty()) {
                    lowVolumeStreak = 0
                    processResults(mergedMatches, usedPartialFallback = finalMatches.isEmpty())
                } else {
                    handleNoTranscription()
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
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, recognitionLocale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Extended silence windows so soft/slow speech from deaf children isn't cut off early
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
            // API 33+: bias the recognizer toward the target word so unclear/soft
            // pronunciation has a better chance of being transcribed correctly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && targetText.isNotBlank()) {
                putExtra(RecognizerIntent.EXTRA_BIASING_STRINGS, arrayListOf(targetText))
            }
        }

        try {
            speechRecognizer?.startListening(intent)
            isListening = true
            setMicActive()
            binding.tvStatus.text = "🎙️ Mendengarkan..."
            binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.tvResultText.visibility = View.GONE
            binding.tvSyllableHint.visibility = View.GONE
        } catch (e: Exception) {
            isListening = false
            // Recognizer may be in bad state — reinit so next tap works
            reinitSpeechRecognizer()
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

    private fun reinitSpeechRecognizer() {
        try { speechRecognizer?.destroy() } catch (_: Exception) {}
        speechRecognizer = null
        setupSpeechRecognizer()
    }

    private fun resetRecognitionSession() {
        partialMatches = emptyList()
        maxRmsDb = Float.NEGATIVE_INFINITY
        detectedSpeechInSession = false
        speechLevelFrames = 0
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

        // Threshold: 35% match — intentionally very forgiving for deaf/hard-of-hearing children
        when {
            highestScore >= 0.35 -> {
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.text = if (usedPartialFallback) {
                    "🌟 BENAR! Aku tetap bisa menangkap suaramu yang pelan 🌟"
                } else {
                    "🌟 BENAR! HEBAT SEKALI! 🌟"
                }
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.textSize = 18f
                binding.tvSyllableHint.visibility = View.GONE
                detectedButNoTextCount = 0
            }
            highestScore >= 0.2 -> {
                // Close but not quite — show syllable breakdown as a hint
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.text = "Hampir! Coba ucapkan suku kata satu per satu ya 😊 (Percobaan ke-$attemptCount)"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.textSize = 14f
                showSyllableHint()
            }
            attemptCount >= 3 -> {
                // After 3 attempts, be very encouraging and lower the bar
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.text = "👏 Bagus sekali sudah mencoba ${attemptCount}x! Kamu hebat! 👏"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.textSize = 16f
                binding.tvSyllableHint.visibility = View.GONE
            }
            else -> {
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.text = "Coba lagi, dekatkan HP ke mulut ya 😊 (Percobaan ke-$attemptCount)"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.textSize = 14f
                showSyllableHint()
            }
        }
    }

    /**
     * Returns true if the child made a real vocalization this session.
     * Stricter than heardSomethingInSession() — requires onBeginningOfSpeech AND sustained RMS.
     */
    private fun hadMeaningfulSpeech(): Boolean {
        return detectedSpeechInSession && (speechLevelFrames >= 5 || maxRmsDb >= 3.5f)
    }

    /**
     * Called when speech was heard (or attempted) but STT produced zero transcription.
     * Distinguishes "truly silent" from "child spoke but STT couldn't decode it".
     * After 3 vocalization attempts the child receives full encouragement credit.
     */
    private fun handleNoTranscription() {
        if (!hadMeaningfulSpeech()) {
            // Nothing was actually spoken — guide the child to speak
            if (!heardSomethingInSession()) lowVolumeStreak++
            binding.tvStatus.text = when {
                lowVolumeStreak >= 2 ->
                    "Pastikan mikrofon tidak tertutup dan dekatkan HP ke mulut ya 📱"
                heardSomethingInSession() ->
                    "Suaramu sangat pelan. Coba lebih dekat ke mic dan bicara lebih keras ya 😊"
                else ->
                    "Belum terdengar suaramu. Tekan 🎤 dan langsung bicara ya 😊"
            }
            binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
            return
        }

        // Child DID speak — STT just couldn't recognize it as a word
        detectedButNoTextCount++
        attemptCount++
        lowVolumeStreak = 0

        binding.tvResultText.visibility = View.VISIBLE
        when {
            detectedButNoTextCount >= 3 -> {
                // Give full credit — child has been consistently trying
                binding.tvResultText.text = "🔊 Suaramu terdengar!"
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.text = "🌟 LUAR BIASA! Kamu sudah berani berbicara! Terus semangat! 🌟"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                binding.tvStatus.textSize = 18f
                binding.tvSyllableHint.visibility = View.GONE
                detectedButNoTextCount = 0
            }
            else -> {
                binding.tvResultText.text = "🔊 Suaramu terdengar! Tapi belum bisa terbaca."
                binding.tvResultText.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.text = "Coba ucapkan suku kata perlahan satu per satu ya 😊 ($detectedButNoTextCount/3)"
                binding.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"))
                binding.tvStatus.textSize = 14f
                showSyllableHint()
            }
        }
    }

    /**
     * Shows a syllable breakdown of the target word as a pronunciation guide.
     * Uses a simple vowel-cluster heuristic sufficient for Indonesian words.
     */
    private fun showSyllableHint() {
        if (_binding == null || targetText.isBlank()) return
        val syllables = splitSyllablesId(targetText)
        if (syllables.size > 1) {
            binding.tvSyllableHint.text = "Coba ucapkan: ${syllables.joinToString(" - ")}"
            binding.tvSyllableHint.visibility = View.VISIBLE
        }
    }

    /**
     * Simple Indonesian syllable splitter:
     * splits on vowel+consonant+vowel boundaries (CV pattern).
     */
    private fun splitSyllablesId(word: String): List<String> {
        val vowels = "aiueoAIUEO"
        val lower = word.trim()
        if (lower.length <= 2) return listOf(lower)
        val result = mutableListOf<String>()
        var start = 0
        var i = 1
        while (i < lower.length - 1) {
            val prev = lower[i - 1]
            val curr = lower[i]
            val next = lower[i + 1]
            // Split after a vowel if followed by a consonant then vowel (V|CV)
            if (prev in vowels && curr !in vowels && next in vowels) {
                result.add(lower.substring(start, i))
                start = i
            }
            i++
        }
        result.add(lower.substring(start))
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try { speechRecognizer?.destroy() } catch (_: Exception) {}
        speechRecognizer = null
        _binding = null
    }
}
