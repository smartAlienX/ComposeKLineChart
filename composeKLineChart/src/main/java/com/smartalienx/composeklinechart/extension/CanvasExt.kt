package com.smartalienx.composeklinechart.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas

fun Canvas.drawCandle(top: Float, rect: Rect, bottom: Float, paint: Paint) {
    val offset = Offset(rect.topCenter.x, top)
    this.drawRect(rect, paint)
    this.drawLine(rect.topCenter, offset, paint)
    this.drawLine(rect.bottomCenter, offset.copy(y = bottom), paint)
}

fun Canvas.drawTextWithBackground(
    text: String,
    x: Float,
    y: Float,
    textPaint: android.graphics.Paint,
    backgroundColor: Int,
    horizontalPadding: Float = 0F,
    verticalPadding: Float = 0F
) {

    val bounds = android.graphics.Rect()
    textPaint.getTextBounds(text, 0, text.length, bounds)
    nativeCanvas.drawRect(
        x,
        y + bounds.top - verticalPadding,
        x + bounds.width() + horizontalPadding * 2,
        y + bounds.bottom + verticalPadding,
        android.graphics.Paint().apply {
            this.color = backgroundColor
            this.isAntiAlias = true
        }
    )

    nativeCanvas.drawText(text, x + horizontalPadding, y, textPaint)
}