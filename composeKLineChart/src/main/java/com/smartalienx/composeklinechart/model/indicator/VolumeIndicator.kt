package com.smartalienx.composeklinechart.model.indicator

import com.smartalienx.composeklinechart.model.config.ChartConfig

class VolumeIndicator(
    override val yAxisConfig: ChartConfig.YAxis = ChartConfig.YAxis(count = 3),
    override val topSpaceDp: Float = 0f
) : Indicator {

    override val series: List<IndicatorSeries.Volume> = listOf(IndicatorSeries.Volume())
    override val id: String = Indicator.VOLUME
    override val isAddToMainChart: Boolean = false

}