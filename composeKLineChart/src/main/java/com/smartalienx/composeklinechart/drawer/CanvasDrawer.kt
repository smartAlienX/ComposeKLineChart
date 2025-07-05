package com.smartalienx.composeklinechart.drawer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.model.config.ChartConfig

interface CanvasDrawer {

    abstract fun onPreDraw(rect: Rect, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource)

    abstract fun onDraw(canvas: Canvas, rect: Rect, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataSource: KLineDataSource)
}