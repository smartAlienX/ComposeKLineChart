package com.smartalienx.composeklinechart.drawer.indicatordrawer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.datasource.SeriesData
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.grid.DefaultGridDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.grid.GridDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.DefaultYAxisDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisValueConversion
import com.smartalienx.composeklinechart.model.config.ChartConfig
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries
import com.smartalienx.composeklinechart.model.indicator.SMAIndicator

class SMAIndicatorDrawer(
    private val yAxisDrawer: YAxisDrawerDelegate = DefaultYAxisDrawer(),
    private val gridDrawer: GridDrawerDelegate = DefaultGridDrawer(),
) : MainIndicatorCanvasDrawer<SMAIndicator>, YAxisValueConversion by yAxisDrawer {

    private var mainYAxisValueConversion: YAxisValueConversion? = null

    private val paint by lazy { Paint() }

    override fun setYAxisValueConversion(yAxisValueConversion: YAxisValueConversion) {
        mainYAxisValueConversion = yAxisValueConversion
    }

    override fun onPreDraw(rect: Rect, xAxisTimeConversion: XAxisTimeConversion, indicator: SMAIndicator, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {
        if (indicator.isAddToMainChart.not()) {

            val indexStart = canvasParams.getIndexStart()
            val indexEnd = canvasParams.getIndexEnd()

            val seriesData = dataSource.getSeriesData<IndicatorSeries.SMA, SeriesData>(indicator.series, indexStart, indexEnd)
            val values = seriesData?.values?.flatten()?.mapNotNull { it?.value }
            val yMaxValue = values?.maxOrNull() ?: 0f
            val yMinValue = values?.minOrNull() ?: 0f

            yAxisDrawer.setYAxisRange(rect.top, rect.bottom)
            yAxisDrawer.setYAxisMinMaxValue(
                minValue = yMinValue,
                maxValue = yMaxValue
            )
        }
    }

    override fun onDraw(canvas: Canvas, rect: Rect, xAxisTimeConversion: XAxisTimeConversion, indicator: SMAIndicator, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {
        val indexStart = canvasParams.getIndexStart()
        val indexEnd = canvasParams.getIndexEnd()
        val seriesData = dataSource.getSeriesData<IndicatorSeries.SMA, SeriesData>(indicator.series, indexStart, indexEnd)

        val yAxisValueConversion = (if (indicator.isAddToMainChart) mainYAxisValueConversion else yAxisDrawer)
            ?: return

        if (indicator.isAddToMainChart.not()) {
            gridDrawer.onDraw(canvas, rect, xAxisTimeConversion, yAxisValueConversion, canvasParams, config.grid, config.timeAxis, indicator.yAxisConfig)
        }

        seriesData?.forEach { (series, dataList) ->
            val color = series.color
            val strokeWidth = series.lineWidthDp * canvasParams.density

            val path = Path()
            dataList.forEachIndexed { i, point ->

                val y = point?.value?.let { yAxisValueConversion.valueToY(it) }
                    ?: return@forEachIndexed
                val x = xAxisTimeConversion.timeToX(point.time)

                if (path.isEmpty) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            paint.apply {
                this.color = Color(color)
                this.strokeWidth = strokeWidth
                this.isAntiAlias = true
                this.style = PaintingStyle.Stroke
            }

            canvas.drawPath(path = path, paint = paint)
        }

        if (indicator.isAddToMainChart.not()) {
            yAxisDrawer.drawYAxis(canvas, rect, canvasParams, config.mainChart.yAxis, config)
        }
    }
}