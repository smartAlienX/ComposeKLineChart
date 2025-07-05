package com.smartalienx.composeklinechart.extension

internal fun android.graphics.Paint.getTextHeight(): Float {
    return fontMetrics.descent - fontMetrics.ascent
}