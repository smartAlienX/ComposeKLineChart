package com.smartalienx.composeklinechart.drawer.drawerdelegate.charttype

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisValueConversion
import com.smartalienx.composeklinechart.drawer.indexRange
import com.smartalienx.composeklinechart.extension.drawCandle
import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.config.ChartConfig

interface CandlesDrawerDelegate : ChartTypeDrawerDelegate

class DefaultCandlesDrawer : CandlesDrawerDelegate {

    private val candlePaint by lazy {
        Paint().apply { isAntiAlias = true }
    }

    override fun onDraw(canvas: Canvas, yAxisValueConversion: YAxisValueConversion, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataList: List<BarData>) {

        val candleWidth = canvasParams.getCandleWidthWithScale()

        candlePaint.strokeWidth = 1f * canvasParams.density * canvasParams.getScale()
        if (candlePaint.strokeWidth < 1f) candlePaint.strokeWidth = 1f

        for (index in canvasParams.indexRange) {

            val candle = dataList[index]

            val (rectTop, rectBottom) = if (candle.open > candle.close) {
                yAxisValueConversion.valueToY(candle.open) to yAxisValueConversion.valueToY(candle.close)
            } else {
                yAxisValueConversion.valueToY(candle.close) to yAxisValueConversion.valueToY(candle.open)
            }
            val rectLeft = xAxisTimeConversion.timeToX(candle.time) - candleWidth / 2
            val rectRight = rectLeft + candleWidth

            candlePaint.color = if (candle.open > candle.close) Color(config.downColor) else Color(config.upColor)

            canvas.drawCandle(
                top = yAxisValueConversion.valueToY(candle.high),
                rect = Rect(
                    left = rectLeft,
                    top = rectTop,
                    right = rectRight,
                    bottom = rectBottom
                ),
                bottom = yAxisValueConversion.valueToY(candle.low),
                paint = candlePaint
            )
        }
    }
}