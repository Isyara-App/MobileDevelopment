package com.example.isyara.util

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.example.isyara.R
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
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

        // Create a MediaPipe MPImage from the bitmap
        val mpImage = BitmapImageBuilder(image).build()
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(imageRotation)
            .build()
        
        val results = objectDetector?.detect(mpImage, imageProcessingOptions)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener?.onResults(
            results,
            inferenceTime,
            mpImage.height,
            mpImage.width
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