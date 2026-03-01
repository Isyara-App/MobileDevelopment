package com.example.isyara.util

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.isyara.R
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.util.*
import kotlin.math.max

class OverlayViewFront(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f
        textBackgroundPaint.alpha = 180

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.primary)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            var left = boundingBox.left * scaleFactor
            var right = boundingBox.right * scaleFactor

            // Flip horizontal for front camera
            left = canvas.width.toFloat() - (left * 0.75f)
            right = canvas.width.toFloat() - right
            Log.d("OverlayViewFront", "left: $left, width: ${canvas.width}, right: $right")

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            if (result.categories.isNotEmpty()) {
                val category = result.categories[0]
                // Draw text for detected object
                val drawableText = "${category.label} ${String.format("%.0f%%", category.score * 100)}"

                // Hitung posisi teks
                textPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                val textWidth = bounds.width()
                val textHeight = bounds.height()
                val textX = left - textWidth
                val textY = top + bounds.height()

                canvas.drawRect(
                    left,
                    top,
                    left - textWidth - BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )

                canvas.drawText(drawableText, textX - (BOUNDING_RECT_TEXT_PADDING / 2), textY, textPaint)
            }
        }
    }

    fun setResults(
        detectionResults: MutableList<Detection>?,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults ?: LinkedList<Detection>()
        // PreviewView is in FILL_START mode
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 16
    }
}
