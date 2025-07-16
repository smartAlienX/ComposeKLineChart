package com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.drawer.CanvasDrawer
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.indexRange
import com.smartalienx.composeklinechart.extension.getTextHeight
import com.smartalienx.composeklinechart.model.TimeInterval
import com.smartalienx.composeklinechart.model.config.ChartConfig
import kotlin.math.ceil

class TimeAxisDrawer : CanvasDrawer {

    private val nativePaint by lazy { android.graphics.Paint() }

    private var timeInterval: TimeInterval = TimeInterval.Minute(1)

    override fun onPreDraw(rect: Rect, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {

    }

    override fun onDraw(
        canvas: Canvas,
        rect: Rect,
        xAxisTimeConversion: XAxisTimeConversion,
        config: ChartConfig,
        canvasParams: CanvasParams,
        dataSource: KLineDataSource
    ) {
        val barList = dataSource.getKLineData()
        val timeFormat = config.timeAxis.timeFormat
        val candleUnitWidth = canvasParams.getCandleUnitWithScale()

        val timeSpace = canvasParams.getWidth() / config.timeAxis.count
        val timeIntervalQuantity = ceil(timeSpace / candleUnitWidth).toInt()

        updatePaintStyle(config, canvasParams)

        val y = rect.top + rect.height / 2 + nativePaint.getTextHeight() / 2

        for (i in canvasParams.indexRange) {
            if (i % timeIntervalQuantity != 0) continue
            val bar = barList.getOrNull(i) ?: continue

            val text = timeFormat.format(timeInterval, bar.time)
            val x = xAxisTimeConversion.timeToX(bar.time)

            canvas.nativeCanvas.drawText(text, x, y, nativePaint)
        }
    }

    private fun updatePaintStyle(config: ChartConfig, canvasParams: CanvasParams) {
        nativePaint.apply {
            isAntiAlias = true
            textSize = config.timeAxis.textSizeSp * canvasParams.density
            color = config.timeAxis.textColor
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
}