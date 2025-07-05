package com.smartalienx.composeklinechart.drawer.indicatordrawer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisValueConversion
import com.smartalienx.composeklinechart.model.config.ChartConfig
import com.smartalienx.composeklinechart.model.indicator.Indicator

interface IndicatorCanvasDrawer<I : Indicator> {

    abstract fun onPreDraw(rect: Rect, xAxisTimeConversion: XAxisTimeConversion, indicator: I, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource)

    abstract fun onDraw(canvas: Canvas, rect: Rect, xAxisTimeConversion: XAxisTimeConversion, indicator: I, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource)
}

interface MainIndicatorCanvasDrawer<I : Indicator> : IndicatorCanvasDrawer<I> {
    fun setYAxisValueConversion(yAxisValueConversion: YAxisValueConversion)
}