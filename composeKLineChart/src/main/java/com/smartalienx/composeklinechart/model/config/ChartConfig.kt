package com.smartalienx.composeklinechart.model.config

import com.smartalienx.composeklinechart.ChartDefaultColor
import com.smartalienx.composeklinechart.format.DefaultTimeFormat
import com.smartalienx.composeklinechart.format.TimeFormat
import java.util.TimeZone

data class ChartConfig(
    val timeZone: TimeZone = TimeZone.getDefault(),
    val mainChart: MainChart = MainChart(),
    val subChartScale: Float = 0.7f,
    val grid: Grid = Grid(),
    val upColor: Int = ChartDefaultColor.UP_RED,
    val downColor: Int = ChartDefaultColor.DOWN_GREEN,
    val timeAxis: TimeAxis = TimeAxis(timeFormat = DefaultTimeFormat(timeZone)),
) {

    data class MainChart(
        val kLineType: KLineType = KLineType.Candle,
        val topSpaceDp: Float = 20f,
        val bottomSpaceDp: Float = 20f,
        val yAxis: YAxis = YAxis()
    )

    data class YAxis(
        val count: Int = 5,
        val textSizeSp: Float = 12f,
        val textColor: Int = ChartDefaultColor.TEXT_GRAY_66
    )

    data class TimeAxis(
        val count: Int = 5,
        val textSizeSp: Float = 12f,
        val textColor: Int = ChartDefaultColor.TEXT_GRAY_66,
        val heightDp: Float = 24f,
        val timeFormat: TimeFormat = DefaultTimeFormat(TimeZone.getDefault())
    )

    data class Grid(
        val lineColor: Int = ChartDefaultColor.LINE_COLOR,
        val lineWidth: Float = 1f,
        val isShowHorizontal: Boolean = true,
        val isShowVertical: Boolean = true
    )
}

sealed interface KLineType {

    data object Line : KLineType

    data object Candle : KLineType
}