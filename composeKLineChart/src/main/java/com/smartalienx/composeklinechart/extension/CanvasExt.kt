package com.smartalienx.composeklinechart.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint

fun Canvas.drawCandle(top: Float, rect: Rect, bottom: Float, paint: Paint) {
    val offset = Offset(rect.topCenter.x, top)
    this.drawRect(rect, paint)
    this.drawLine(rect.topCenter, offset, paint)
    this.drawLine(rect.bottomCenter, offset.copy(y = bottom), paint)
}