package com.smartalienx.composeklinechart.drawer.indicatordrawer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import com.smartalienx.composeklinechart.ChartDefaultColor
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.datasource.SeriesData
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.grid.DefaultGridDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.grid.GridDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.DefaultYAxisDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisDrawerDelegate
import com.smartalienx.composeklinechart.model.config.ChartConfig
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries
import com.smartalienx.composeklinechart.model.indicator.VolumeIndicator

class VolumeIndicatorDrawer(
    private val yAxisDrawer: YAxisDrawerDelegate = DefaultYAxisDrawer(),
    private val gridDrawer: GridDrawerDelegate = DefaultGridDrawer(),
) : IndicatorCanvasDrawer<VolumeIndicator> {

    private val paint by lazy { Paint() }

    override fun onPreDraw(rect: Rect, xAxisTimeConversion: XAxisTimeConversion, indicator: VolumeIndicator, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {

        val indexStart = canvasParams.getIndexStart()
        val indexEnd = canvasParams.getIndexEnd()

        val seriesData = dataSource.getSeriesData<IndicatorSeries.Volume, SeriesData>(indicator.series, indexStart, indexEnd)
        val values = seriesData?.values?.flatten()?.mapNotNull { it?.value }
        val yMaxValue = (values?.maxOrNull() ?: 0f)
        val yMinValue = (values?.minOrNull()  ?: 0f)

        yAxisDrawer.setYAxisRange(rect.top, rect.bottom)
        yAxisDrawer.setYAxisMinMaxValue(
            minValue = 0f,
            maxValue = (yMaxValue*1.1f).toInt().toFloat()
        )
    }

    override fun onDraw(canvas: Canvas, rect: Rect, xAxisTimeConversion: XAxisTimeConversion, indicator: VolumeIndicator, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {
        val indexStart = canvasParams.getIndexStart()
        val indexEnd = canvasParams.getIndexEnd()
        val candleWidth = canvasParams.getCandleWidthWithScale()

        val seriesData = dataSource.getSeriesData<IndicatorSeries.Volume, SeriesData>(indicator.series, indexStart, indexEnd)

        gridDrawer.onDraw(canvas, rect, xAxisTimeConversion, yAxisDrawer, canvasParams, config.grid, config.timeAxis, indicator.yAxisConfig)

        seriesData?.forEach { (series, dataList) ->

            paint.apply {
                this.color = Color(ChartDefaultColor.DOWN_GREEN)
                this.isAntiAlias = true
            }

            dataList.filterNotNull().forEachIndexed { i, point ->
                val x = xAxisTimeConversion.timeToX(point.time) - candleWidth / 2
                val aa = Rect(
                    top = yAxisDrawer.valueToY(point.value),
                    bottom = rect.bottom,
                    left = x,
                    right = x + candleWidth
                )
                canvas.drawRect(
                    rect = Rect(
                        top = yAxisDrawer.valueToY(point.value),
                        bottom = rect.bottom,
                        left = x,
                        right = x + candleWidth
                    ),
                    paint = paint
                )
            }
        }

        yAxisDrawer.drawYAxis(canvas, rect, canvasParams, indicator.yAxisConfig, config)
    }
}