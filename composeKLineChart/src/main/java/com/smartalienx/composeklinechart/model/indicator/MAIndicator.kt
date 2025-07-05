package com.smartalienx.composeklinechart.model.indicator

import com.smartalienx.composeklinechart.model.config.ChartConfig

abstract class MAIndicator(
    override val series: List<Series>,
    override val yAxisConfig: ChartConfig.YAxis = ChartConfig.YAxis(count = 3)
) : Indicator {

    data class Series(
        val period: Int,
        val color: Int,
        val lineWidthDp: Float = 1F
    ) : Indicator.Series {
        override val seriesId: String = Indicator.Series.SMA

        override fun paramsCode(): String {
            return period.toString()
        }
    }
}