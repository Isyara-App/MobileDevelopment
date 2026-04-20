package com.isyara.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import com.isyara.app.R
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

class ObjectDetectorHelper(
    var threshold: Float = 0.4f, // Menurunkan batas sedikit agar lebih peka mendeteksi
    var maxResults: Int = 3,     // Ditingkatkan agar kandidat yang terhalang skor wajah tetap keluar
    var modelName: String = "abjad_v3.tflite",
    val context: Context,
    val objectDetectorListener: DetectorListener?
) {
    var numThreads: Int = 2
    private val TAG = "ObjectDetectionHelper"
    private var objectDetector: ObjectDetector? = null
    private val lock = Any()

    init {
        setupObjectDetector()
    }

    fun clearObjectDetector() {
        synchronized(lock) {
            objectDetector?.close()
            objectDetector = null
        }
    }

    fun updateModel(newModelName: String) {
        synchronized(lock) {
            this.modelName = newModelName
            objectDetector?.close()
            objectDetector = null
            setupObjectDetector()
        }
    }

    fun setupObjectDetector() {
        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(Delegate.CPU)
            .setModelAssetPath(modelName)

        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener(this::returnLivestreamResult)
            .setErrorListener(this::returnLivestreamError)

        try {
            objectDetector = ObjectDetector.createFromOptions(context, optionsBuilder.build())
        } catch (e: Exception) {
            objectDetectorListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, "MediaPipe failed to load model with error: " + e.message)
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        synchronized(lock) {
            val detector = objectDetector ?: return

            // Physically rotate the bitmap so MediaPipe sees an upright image.
            val rotatedBitmap = if (imageRotation != 0) {
                val matrix = Matrix().apply { postRotate(imageRotation.toFloat()) }
                Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
            } else {
                image
            }

            val mpImage = BitmapImageBuilder(rotatedBitmap).build()

            // Mode LIVE_STREAM me-wajibkan pengiriman via asinkron dengan waktu frame (timestamp)
            detector.detectAsync(mpImage, SystemClock.uptimeMillis())
        }
    }

    private fun returnLivestreamResult(
        result: ObjectDetectorResult,
        image: com.google.mediapipe.framework.image.MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()
        objectDetectorListener?.onResults(result, inferenceTime, image.height, image.width)
    }

    private fun returnLivestreamError(error: RuntimeException) {
        objectDetectorListener?.onError(error.message ?: "An unknown error has occurred")
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: ObjectDetectorResult?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }
}