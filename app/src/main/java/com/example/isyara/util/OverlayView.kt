package com.example.isyara.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import java.text.NumberFormat
import kotlin.math.max

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var textPaint = Paint()
    private var textBackgroundPaint = Paint()

    private var results: List<Classifications>? = listOf()
    private var scaleFactor: Float = 1f

    init {
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f
    }

    fun setResults(
        classificationResults: List<Classifications>?,
        imageHeight: Int,
        imageWidth: Int
    ) {
        results = classificationResults ?: emptyList()

        // Calculate the scale factor based on the image size and the view size.
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
        invalidate() // Trigger a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Loop through each classification result and draw it
        results?.forEachIndexed { index, classification ->
            val categories = classification.categories.sortedByDescending { it.score }
            val topCategory = categories.firstOrNull()

            // Draw the category label and score
            topCategory?.let {
                val label = it.label
                val score = NumberFormat.getPercentInstance().format(it.score)

                // Set the background for the text
                val textWidth = textPaint.measureText(label + " " + score)
                val textHeight = textPaint.textSize

                // Draw text background
                canvas.drawRect(
                    50f, (index * (textHeight + 20)).toFloat(),
                    50f + textWidth, (index * (textHeight + 20) + textHeight).toFloat(),
                    textBackgroundPaint
                )

                // Draw the label and score
                canvas.drawText(
                    "$label $score",
                    50f,
                    (index * (textHeight + 20) + textHeight).toFloat(),
                    textPaint
                )
            }
        }
    }
}
