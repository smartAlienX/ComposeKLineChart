package com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.extension.getTextHeight
import com.smartalienx.composeklinechart.model.config.ChartConfig

interface YAxisDrawerDelegate : YAxisValueConversion {

    fun drawYAxis(canvas: Canvas, rect: Rect, canvasParams: CanvasParams, yAxisConfig: ChartConfig.YAxis, chartConfig: ChartConfig)

    fun setYAxisMinMaxValue(minValue: Float, maxValue: Float)

    fun setYAxisRange(topY: Float, bottomY: Float)
}

class DefaultYAxisDrawer : YAxisDrawerDelegate {

    private val yAxisHelper = YAxisHelper()

    private val nativePaint by lazy { android.graphics.Paint() }

    override fun drawYAxis(canvas: Canvas, rect: Rect, canvasParams: CanvasParams, yAxisConfig: ChartConfig.YAxis, chartConfig: ChartConfig) {
        nativePaint.apply {
            isAntiAlias = true
            textSize = yAxisConfig.textSizeSp * canvasParams.density
            color = yAxisConfig.textColor
        }

        val textHeight = nativePaint.getTextHeight()
        for (i in 0 until yAxisConfig.count) {
            val y = rect.top + (rect.height / (yAxisConfig.count - 1)) * i
            val value = chartConfig.valueFormat.formatValue(yAxisHelper.yToValue(y))

            val textY = if (i == 0) y + textHeight else y
            canvas.nativeCanvas.drawText(value, 0f, textY - 2f * canvasParams.density, nativePaint)
        }
    }

    override fun setYAxisMinMaxValue(minValue: Float, maxValue: Float) {
        yAxisHelper.setMinMaxValue(minValue, maxValue)
    }

    override fun setYAxisRange(topY: Float, bottomY: Float) {
        yAxisHelper.setYAxisRange(topY, bottomY)
    }

    override fun valueToY(value: Float): Float {
        return yAxisHelper.valueToY(value)
    }

    override fun yToValue(y: Float): Float {
        return yAxisHelper.yToValue(y)
    }
}