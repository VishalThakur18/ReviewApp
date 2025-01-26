package com.example.myapplication.Views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class DashedLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grey) // Use your desired color
        strokeWidth = 8f // Thickness of the dashed line
        isAntiAlias = true
    }

    private val semiCirclePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grey) // Same as dashed line color
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val dashHeight = 20f // Height of each dash
        val dashGap = 10f // Space between dashes
        var currentY = dashGap

        // Draw the dashed line
        while (currentY < height) {
            canvas.drawLine(width / 2f, currentY, width / 2f, currentY + dashHeight, paint)
            currentY += dashHeight + dashGap
        }

        // Draw the semicircle at the top
        val radius = width / 2f
        val topPath = Path().apply {
            addCircle(width / 2f, 0f, radius, Path.Direction.CCW)
        }
        canvas.drawPath(topPath, semiCirclePaint)

        // Draw the semicircle at the bottom
        val bottomPath = Path().apply {
            addCircle(width / 2f, height.toFloat(), radius, Path.Direction.CCW)
        }
        canvas.drawPath(bottomPath, semiCirclePaint)
    }
}