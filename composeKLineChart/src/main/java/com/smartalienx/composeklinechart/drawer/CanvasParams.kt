package com.smartalienx.composeklinechart.drawer

import android.util.Log
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

class CanvasParams(val density: Float) {

    private var width = 0f
    private var height = 0f

    private var movingDistance = 0f
    private var scale = 1f
    private var dataCount = 0

    private var indexStart = 0
    private var indexEnd = 0
    private var startX = 0f

    var candleSpace = DEFAULT_CANDLE_WIDTH / 2

    fun updateDataCount(dataCount: Int) {
        this.dataCount = dataCount
    }

    fun updateCanvasSize(width: Float, height: Float) {
        if (this.width == width && this.height == height) return
        this.width = width
        this.height = height
    }

    fun updateParamsChange(dataCount: Int, movingDistanceChange: Float, scaleChange: Float) {
        this.dataCount = dataCount

        // drag operation
        if (abs(movingDistanceChange) > 0.01f) {
            this.movingDistance += movingDistanceChange
            return
        }

        // zoom operation
        if (scaleChange != 0f) {

            val oldUnit = getCandleWidthWithScale() + getCandleSpaceWithScale()
            val centerX = width / 2f
            val offsetCount = this.movingDistance / oldUnit
            val centerIndex = (dataCount - offsetCount) - (centerX / oldUnit)

            this.scale = (this.scale * scaleChange).coerceIn(MIN_SCALE, MAX_SCALE)

            val newUnit = getCandleWidthWithScale() + getCandleSpaceWithScale()
            val newOffset = dataCount - centerIndex - (centerX / newUnit)

            this.movingDistance = newOffset * newUnit

            return
        }
    }

    fun getScale(): Float {
        return scale
    }

    fun getWidth(): Float {
        return width
    }

    fun getHeight(): Float {
        return height
    }

    fun getCandleWidthWithScale(): Float {
        return DEFAULT_CANDLE_WIDTH * scale
    }

    fun getCandleSpaceWithScale(): Float {
        return candleSpace * scale
    }

    fun getCandleUnitWithScale(): Float {
        return getCandleWidthWithScale() + getCandleSpaceWithScale()
    }

    fun getIndexStart(): Int {
        return indexStart
    }

    fun getIndexEnd(): Int {
        return indexEnd
    }

    fun getStartX(): Float {
        return startX
    }

    fun calculateStartEndIndex() {
        if (width == 0f) return

        Log.d("CanvasParams", "calculateStartEndIndex:$movingDistance")
        val candleWidth = getCandleWidthWithScale() + getCandleSpaceWithScale()
        val visibleCount = ceil(width / candleWidth).toInt() + 1
        val offsetCount = movingDistance / candleWidth

        indexEnd = (dataCount - 1 - (offsetCount).toInt()).coerceIn(0, dataCount - 1)
        indexStart = (indexEnd - visibleCount).coerceIn(0, indexEnd)

        val fractionalOffset = offsetCount - floor(offsetCount)
        val candleXOffset = fractionalOffset * candleWidth

        val realVisibleCount = indexEnd - indexStart
        startX = width - realVisibleCount * candleWidth

        startX += if (movingDistance < 0) movingDistance else candleXOffset
    }

    companion object {
        const val DEFAULT_CANDLE_WIDTH = 50f

        const val MAX_SCALE = 5f
        const val MIN_SCALE = 0.01f
    }
}

val CanvasParams.indexRange: IntRange get() = getIndexStart()..getIndexEnd()