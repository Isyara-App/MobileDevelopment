package com.example.isyara.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import com.example.isyara.R
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

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

    init {
        setupObjectDetector()
    }

    fun clearObjectDetector() {
        objectDetector?.close()
        objectDetector = null
    }

    fun updateModel(newModelName: String) {
        this.modelName = newModelName
        clearObjectDetector()
        setupObjectDetector()
    }

    fun setupObjectDetector() {
        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(Delegate.CPU)
            .setModelAssetPath(modelName)

        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
            .setRunningMode(RunningMode.IMAGE)

        try {
            objectDetector = ObjectDetector.createFromOptions(context, optionsBuilder.build())
        } catch (e: Exception) {
            objectDetectorListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, "MediaPipe failed to load model with error: " + e.message)
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if (objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()

        // Physically rotate the bitmap so MediaPipe sees an upright image.
        // This guarantees bounding box coordinates match the visual display exactly.
        val rotatedBitmap = if (imageRotation != 0) {
            val matrix = Matrix().apply { postRotate(imageRotation.toFloat()) }
            Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)
        } else {
            image
        }

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        // No rotation hint needed — the bitmap is already upright.
        val results = objectDetector?.detect(mpImage)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        objectDetectorListener?.onResults(
            results,
            inferenceTime,
            rotatedBitmap.height,
            rotatedBitmap.width
        )
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