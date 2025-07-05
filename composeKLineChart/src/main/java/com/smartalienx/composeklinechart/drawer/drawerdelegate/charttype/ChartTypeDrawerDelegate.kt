package com.smartalienx.composeklinechart.drawer.drawerdelegate.charttype

import androidx.compose.ui.graphics.Canvas
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.XAxisTimeConversion
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisDrawerDelegate
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisValueConversion
import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.config.ChartConfig

interface ChartTypeDrawerDelegate {

    fun onDraw(canvas: Canvas, yAxisValueConversion: YAxisValueConversion, xAxisTimeConversion: XAxisTimeConversion, config: ChartConfig, canvasParams: CanvasParams, dataList: List<BarData>)

}