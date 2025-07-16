package com.smartalienx.composeklinechart.model.config

import com.smartalienx.composeklinechart.ChartDefaultColor
import com.smartalienx.composeklinechart.format.DefaultTimeFormat
import com.smartalienx.composeklinechart.format.DefaultValueFormat
import com.smartalienx.composeklinechart.format.TimeFormat
import com.smartalienx.composeklinechart.format.ValueFormat
import com.smartalienx.composeklinechart.model.TimeInterval
import com.smartalienx.composeklinechart.model.charttype.ChartType
import java.util.TimeZone

data class ChartConfig(
    val chartType: ChartType = ChartType.Candle,
    val timeInterval: TimeInterval = TimeInterval.Minute(1),
    val timeZone: TimeZone = TimeZone.getDefault(),
    val mainChart: MainChart = MainChart(),
    val subChartScale: Float = 0.5f,
    val grid: Grid = Grid(),
    val upColor: Int = ChartDefaultColor.UP_RED,
    val downColor: Int = ChartDefaultColor.DOWN_GREEN,
    val timeAxis: TimeAxis = TimeAxis(timeFormat = DefaultTimeFormat(timeZone)),
    val crossHairs: CrossHairs = CrossHairs(),
    val valueFormat: ValueFormat = DefaultValueFormat(),
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
        val lineColor: Int = ChartDefaultColor.GRID_LINE_COLOR,
        val lineWidth: Float = 1f,
        val isShowHorizontal: Boolean = true,
        val isShowVertical: Boolean = true
    )

    data class CrossHairs(
        val isShow: Boolean = true,
        val isMagnetism: Boolean = true,
        val textSizeSp: Float = 12f,
        val textColor: Int = ChartDefaultColor.TEXT_WHITE,
        val textBackgroundColor: Int = ChartDefaultColor.BG_GREY,
        val lineColor: Int = ChartDefaultColor.BG_GREY,
        val lineWidth: Float = 1f,
        val timeFormat: TimeFormat = DefaultTimeFormat(TimeZone.getDefault())
    )
}

sealed interface KLineType {

    data object Line : KLineType

    data object Candle : KLineType
}