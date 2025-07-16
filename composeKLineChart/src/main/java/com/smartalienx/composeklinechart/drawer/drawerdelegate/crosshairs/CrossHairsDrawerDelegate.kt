package com.smartalienx.composeklinechart.drawer.drawerdelegate.crosshairs

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisValueConversion
import com.smartalienx.composeklinechart.extension.drawTextWithBackground
import com.smartalienx.composeklinechart.extension.getTextHeight
import com.smartalienx.composeklinechart.model.config.ChartConfig

interface CrossHairsDrawerDelegate {

    fun onDraw(
        canvas: Canvas,
        contentRect: Rect,
        timeRect: Rect,
        crossHairsPoint: Offset?,
        xAxisTimeConversion: XAxisTimeConversion,
        yAxisValueConversion: YAxisValueConversion?,
        canvasParams: CanvasParams,
        config: ChartConfig,
    )

}

class DefaultCrossHairsDrawer : CrossHairsDrawerDelegate {

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }

    private val textPaint by lazy {
        android.graphics.Paint().apply {
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas, contentRect: Rect, timeRect: Rect, crossHairsPoint: Offset?, xAxisTimeConversion: XAxisTimeConversion, yAxisValueConversion: YAxisValueConversion?, canvasParams: CanvasParams, config: ChartConfig) {
        crossHairsPoint ?: return

        val crossHairsConfig = config.crossHairs
        val timeFormat = crossHairsConfig.timeFormat
        val valueFormat = config.valueFormat
        val textHeight = textPaint.getTextHeight()
        val timeText = timeFormat.format(config.timeInterval, xAxisTimeConversion.xToTime(crossHairsPoint.x))
        val priceText = yAxisValueConversion?.let { conversion ->
            valueFormat.formatValue(conversion.yToValue(crossHairsPoint.y))
        }

        val timeX = if (crossHairsConfig.isMagnetism) {
            xAxisTimeConversion.timeToX(xAxisTimeConversion.xToTime(crossHairsPoint.x))
        } else {
            crossHairsPoint.x
        }

        val bgPadding = 4f * canvasParams.density
        paint.apply {
            color = Color(crossHairsConfig.lineColor)
            strokeWidth = crossHairsConfig.lineWidth * canvasParams.density
        }
        textPaint.apply {
            color = crossHairsConfig.textColor
            textSize = crossHairsConfig.textSizeSp * canvasParams.density
        }

        // horizontal line
        canvas.drawLine(Offset(contentRect.left, crossHairsPoint.y), Offset(contentRect.right, crossHairsPoint.y), paint)
        // vertical line
        canvas.drawLine(Offset(timeX, contentRect.top), Offset(timeX, contentRect.bottom), paint)

        // time text
        val timeContentWidth = textPaint.measureText(timeText) + bgPadding * 2
        val timeLabelY = timeRect.top + timeRect.height / 2 + textHeight / 2
        val timeLabelX = (timeX - timeContentWidth / 2)
            .coerceIn(contentRect.left, contentRect.right - timeContentWidth)
        canvas.drawTextWithBackground(timeText, timeLabelX, timeLabelY, textPaint, crossHairsConfig.textBackgroundColor, bgPadding, bgPadding)

        // price text
        priceText?.also {
            val priceLabelY = (crossHairsPoint.y + textHeight / 2 - bgPadding)
                .coerceIn(contentRect.top + textHeight, contentRect.bottom - bgPadding)
            canvas.drawTextWithBackground(priceText, contentRect.left, priceLabelY, textPaint, crossHairsConfig.textBackgroundColor, bgPadding, bgPadding)
        }
    }
}