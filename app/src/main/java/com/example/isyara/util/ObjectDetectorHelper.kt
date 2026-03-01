package com.example.isyara.util

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.isyara.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector
import java.io.File
import java.lang.IllegalStateException

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var maxResults: Int = 1,
    var modelName: String = "abjad_v3.tflite",
    val context: Context,
    val objectDetectorListener: DetectorListener?
) {
    var numThreads: Int = 2

    private val TAG = "ObjectDetectionHelper"
    private var objectDetector: ObjectDetector? = null
    private val lock = Any()

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupObjectDetector()
        }.addOnFailureListener {
            objectDetectorListener?.onError("TfLiteVision failed to initialize: " + it.message)
        }
    }

    fun clearObjectDetector() {
        synchronized(lock) {
            objectDetector?.close()
            objectDetector = null
        }
    }

    fun updateModel(newModelName: String) {
        this.modelName = newModelName
        clearObjectDetector()
        setupObjectDetector()
    }

    fun setupObjectDetector() {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "setupObjectDetector: TfLiteVision is not initialized yet")
            return
        }

        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        synchronized(lock) {
            try {
                // Load model from assets using createFromFileAndOptions
                objectDetector = ObjectDetector.createFromFileAndOptions(
                    context,
                    modelName,
                    optionsBuilder.build()
                )
            } catch (e: Exception) {
                objectDetectorListener?.onError(context.getString(R.string.image_classifier_failed))
                Log.e(TAG, "TFLite failed to load model with error: " + e.message)
            }
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if (!TfLiteVision.isInitialized()) {
            Log.e(TAG, "detect: TfLiteVision is not initialized yet")
            return
        }

        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        val imageProcessor = ImageProcessor.Builder().add(Rot90Op(-imageRotation / 90)).build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

        val results = synchronized(lock) {
            // Check again inside lock to ensure it wasn't nulled out
            objectDetector?.detect(tensorImage)
        }
        
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener?.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }
}