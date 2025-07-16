package com.smartalienx.composeklinechart.drawer.drawerdelegate.grid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisValueConversion
import com.smartalienx.composeklinechart.drawer.indexRange
import com.smartalienx.composeklinechart.model.config.ChartConfig
import kotlin.math.ceil

interface GridDrawerDelegate {

    fun onDraw(
        canvas: Canvas,
        rect: Rect,
        xAxisTimeConversion: XAxisTimeConversion,
        yAxisValueConversion: YAxisValueConversion,
        canvasParams: CanvasParams,
        gridConfig: ChartConfig.Grid,
        timeAxisConfig: ChartConfig.TimeAxis,
        yAxisConfig: ChartConfig.YAxis
    )
}

class DefaultGridDrawer : GridDrawerDelegate {

    private val gridLinePaint by lazy { Paint() }

    override fun onDraw(
        canvas: Canvas,
        rect: Rect,
        xAxisTimeConversion: XAxisTimeConversion,
        yAxisValueConversion: YAxisValueConversion,
        canvasParams: CanvasParams,
        gridConfig: ChartConfig.Grid,
        timeAxisConfig: ChartConfig.TimeAxis,
        yAxisConfig: ChartConfig.YAxis
    ) {

        updatePaintStyle(gridConfig, canvasParams)
        drawVerticalLine(canvas, rect, xAxisTimeConversion, timeAxisConfig, canvasParams)
        drawHorizontalLine(canvas, rect, yAxisValueConversion, yAxisConfig)
    }

    private fun updatePaintStyle(config: ChartConfig.Grid, canvasParams: CanvasParams) {
        gridLinePaint.apply {
            isAntiAlias = true
            color = Color(config.lineColor)
            strokeWidth = config.lineWidth * canvasParams.density
        }
    }

    private fun drawVerticalLine(canvas: Canvas, rect: Rect, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig.TimeAxis, canvasParams: CanvasParams) {

        val candleUnitWidth = canvasParams.getCandleWidthWithScale() + canvasParams.getCandleSpaceWithScale()
        val timeSpace = canvasParams.getWidth() / config.count
        val timeIntervalQuantity = ceil(timeSpace / candleUnitWidth).toInt()

        for (i in canvasParams.indexRange) {
            if (i % timeIntervalQuantity != 0) continue
            val x = xAxisTimeConversion.indexToX(i)
            canvas.drawLine(Offset(x, rect.top), Offset(x, rect.bottom), gridLinePaint)
        }
    }

    private fun drawHorizontalLine(canvas: Canvas, rect: Rect, yAxisValueConversion: YAxisValueConversion, yAxisConfig: ChartConfig.YAxis) {
        for (i in 0 until yAxisConfig.count) {
            val y = rect.top + (rect.height / (yAxisConfig.count - 1)) * i
            canvas.drawLine(Offset(0f, y), Offset(rect.width, y), gridLinePaint)
        }
    }
}