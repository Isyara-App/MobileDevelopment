package com.isyara.app.util

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.isyara.app.R
import com.google.mediapipe.tasks.components.containers.Detection
import java.util.*
import kotlin.math.max
import kotlin.math.min

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
        results = LinkedList()
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

        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        val scaledWidth = imageWidth * scaleFactor
        val scaledHeight = imageHeight * scaleFactor
        val offsetX = (canvasWidth - scaledWidth) / 2f
        val offsetY = (canvasHeight - scaledHeight) / 2f

        for (result in results) {
            val boundingBox = result.boundingBox()

            // Scale and offset coordinates
            var top = boundingBox.top * scaleFactor + offsetY
            var bottom = boundingBox.bottom * scaleFactor + offsetY
            var left = boundingBox.left * scaleFactor + offsetX
            var right = boundingBox.right * scaleFactor + offsetX

            // Clamp bounding box to stay within screen bounds
            left = max(0f, min(left, canvasWidth))
            top = max(0f, min(top, canvasHeight))
            right = max(0f, min(right, canvasWidth))
            bottom = max(0f, min(bottom, canvasHeight))

            // Skip if box is too small after clamping
            if (right - left < 5f || bottom - top < 5f) continue

            // Draw bounding box around detected objects
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            if (result.categories().isNotEmpty()) {
                val category = result.categories()[0]
                // Create text to display alongside detected objects
                val drawableText = "${category.categoryName()} ${String.format("%.0f%%", category.score() * 100)}"

                // Measure text bounds
                textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                val textWidth = bounds.width()
                val textHeight = bounds.height()

                // Calculate label position - ensure it stays within screen
                val labelPadding = BOUNDING_RECT_TEXT_PADDING
                var labelLeft = left
                var labelTop = top - textHeight - labelPadding

                // If label would go above screen, place it below the top of bounding box
                if (labelTop < 0f) {
                    labelTop = top
                }

                // If label would go beyond right edge, shift it left
                var labelRight = labelLeft + textWidth + labelPadding
                if (labelRight > canvasWidth) {
                    labelLeft = canvasWidth - textWidth - labelPadding
                    labelRight = canvasWidth
                }

                // Ensure label left doesn't go negative
                if (labelLeft < 0f) {
                    labelLeft = 0f
                    labelRight = (textWidth + labelPadding).toFloat()
                }

                val labelBottom = labelTop + textHeight + labelPadding

                // Draw rounded rect behind display text
                val textBgRect = RectF(labelLeft, labelTop, labelRight, labelBottom)
                canvas.drawRoundRect(textBgRect, 8f, 8f, textBackgroundPaint)

                // Draw text for detected object
                canvas.drawText(
                    drawableText,
                    labelLeft + labelPadding / 2f,
                    labelBottom - labelPadding / 2f,
                    textPaint
                )
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
