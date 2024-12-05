package com.example.isyara.ui.translate

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.isyara.databinding.FragmentTranslateBinding
import com.example.isyara.util.ImageClassifierHelper
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors

class TranslateFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentTranslateBinding? = null
    private val binding get() = _binding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val resultList = mutableListOf<String>()
    private lateinit var textToSpeech: TextToSpeech


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTranslateBinding.inflate(inflater, container, false)
        textToSpeech = TextToSpeech(requireContext(), this)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemUI()
        startCamera()

        binding.btnTextToSpeech.setOnClickListener {
            val textToSpeak = binding.tvResult.text.toString()
            if (textToSpeak.isNotEmpty()) {
                speakOut(textToSpeak)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = textToSpeech.setLanguage(Locale("id", "ID"))
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported or missing data")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    private fun startCamera() {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    activity?.runOnUiThread {
                        if (isAdded) {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onResults(
                    results: List<Classifications>?,
                    inferenceTime: Long,
                    imageHeight: Int,
                    imageWidth: Int,
                ) {
                    activity?.runOnUiThread {
                        if (isAdded) {
                            results?.let { it ->
                                if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                    println(it)


                                    val sortedCategories =
                                        it[0].categories.sortedByDescending { it?.score }
                                    val displayResult =
                                        sortedCategories.joinToString("\n") {
                                            "${it.label} " + NumberFormat.getPercentInstance()
                                                .format(it.score).trim()
                                        }

                                    binding.tvResult.text = displayResult
                                    binding.btnAdd.setOnClickListener {
                                        // Menambahkan label ke list
                                        val label = sortedCategories.firstOrNull()?.label ?: ""
                                        resultList.add(label)

                                        // Mengupdate textBox dengan list yang ada
                                        binding.textBox.text = resultList.joinToString(" ")


                                    }

                                    binding.btnDelete.setOnClickListener {
                                        resultList.clear()
                                        binding.textBox.text = ""
                                    }

                                    binding.btnTextToSpeech.setOnClickListener {
                                        val textToSpeak = binding.textBox.text.toString()
                                        if (textToSpeak.isNotEmpty()) {
                                            speakOut(textToSpeak)
                                        }
                                    }
                                } else {
                                    binding.tvResult.text = ""
                                }
                            }
                        }
                    }
                }
            }
        )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.cameraView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                imageClassifierHelper.classifyImage(image)
            }

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    requireActivity(),
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    requireActivity(),
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("TranslateFragment", "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "Translate Fragment"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
    }
}