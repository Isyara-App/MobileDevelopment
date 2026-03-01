package com.example.isyara.util

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.isyara.R
import com.google.mediapipe.tasks.components.containers.Detection
import java.util.*
import kotlin.math.max

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

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
        textBackgroundPaint.alpha = 180 // slight transparency

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.primary)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val scaledWidth = imageWidth * scaleFactor
        val scaledHeight = imageHeight * scaleFactor
        val offsetX = (width - scaledWidth) / 2f
        val offsetY = (height - scaledHeight) / 2f

        for (result in results) {
            val boundingBox = result.boundingBox()

            val top = boundingBox.top * scaleFactor + offsetY
            val bottom = boundingBox.bottom * scaleFactor + offsetY
            val left = boundingBox.left * scaleFactor + offsetX
            val right = boundingBox.right * scaleFactor + offsetX

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            if (result.categories().isNotEmpty()) {
                val category = result.categories()[0]
                // Create text to display alongside detected objects
                val drawableText = "${category.categoryName()} ${String.format("%.0f%%", category.score() * 100)}"

                // Draw rect behind display text
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                val textWidth = bounds.width()
                val textHeight = bounds.height()
                
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )

                // Draw text for detected object
                canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
            }
        }
    }

    fun setResults(
        detectionResults: List<Detection>?,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        results = detectionResults ?: LinkedList<Detection>()
        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        // PreviewView defaults to FILL_CENTER. We must scale and subsequently offset.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 16
    }
}
