package com.smartalienx.composeklinechart.drawer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.drawer.drawerdelegate.charttype.CandlesDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.charttype.DefaultCandlesDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.grid.DefaultGridDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.grid.GridDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.DefaultYAxisDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisMinMaxValue
import com.smartalienx.composeklinechart.model.charttype.ChartType
import com.smartalienx.composeklinechart.model.config.ChartConfig
import com.smartalienx.composeklinechart.model.indicator.Indicator

class MainChartDrawer(
    val yAxisDrawer: YAxisDrawerDelegate = DefaultYAxisDrawer(),
    private val gridDrawer: GridDrawerDelegate = DefaultGridDrawer(),
    private val candlesDrawerDelegate: CandlesDrawerDelegate = DefaultCandlesDrawer()
) : CanvasDrawer, YAxisMinMaxValue {

    private var indicatorList: List<Indicator> = emptyList()
    private var chartType: ChartType = ChartType.Candle

    private var yMaxValue: Float = Float.MIN_VALUE
    private var yMinValue: Float = Float.MAX_VALUE

    fun setMainIndicators(indicators: List<Indicator>) {
        if (indicators == indicatorList) return
        indicatorList = indicators.filter { it.isAddToMainChart }
    }

    fun setChartType(chartType: ChartType) {
        this.chartType = chartType
    }

    override fun onPreDraw(rect: Rect, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {
        val barList = dataSource.getKLineData()
        val visibilityDataList = barList.subList(canvasParams.getIndexStart(), canvasParams.getIndexEnd() + 1)
        yMaxValue = visibilityDataList.maxOf { it.high }
        yMinValue = visibilityDataList.minOf { it.low }

        val topSpace = config.mainChart.topSpaceDp * canvasParams.density
        val bottomSpace = config.mainChart.bottomSpaceDp * canvasParams.density

        yAxisDrawer.setYAxisRange(rect.top + topSpace, rect.bottom - bottomSpace)
        yAxisDrawer.setYAxisMinMaxValue(
            minValue = yMinValue,
            maxValue = yMaxValue
        )
    }

    override fun onDraw(canvas: Canvas, rect: Rect, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource) {
        val barList = dataSource.getKLineData()
        if (barList.isEmpty()) return

        gridDrawer.onDraw(canvas, rect, xAxisTimeConversion, yAxisDrawer, canvasParams, config.grid, config.timeAxis, config.mainChart.yAxis)

        candlesDrawerDelegate.onDraw(
            canvas = canvas,
            config = config,
            canvasParams = canvasParams,
            xAxisTimeConversion = xAxisTimeConversion,
            yAxisValueConversion = yAxisDrawer,
            dataList = barList
        )

        yAxisDrawer.drawYAxis(canvas, rect, canvasParams, config.mainChart.yAxis, config)
    }

    override fun getYMaxValue(): Float {
        return yMaxValue
    }

    override fun getYMinValue(): Float {
        return yMinValue
    }
}
