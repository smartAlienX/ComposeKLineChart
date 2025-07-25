package com.smartalienx.composeklinechart.model.indicator

import com.smartalienx.composeklinechart.model.SpaceDp
import com.smartalienx.composeklinechart.model.config.ChartConfig

data class SMAIndicator(
    override val series: List<IndicatorSeries.SMA>,
    override val yAxisConfig: ChartConfig.YAxis = ChartConfig.YAxis(count = 3),
    override val isAddToMainChart: Boolean = true,
    override val spaceDp: SpaceDp = SpaceDp.indicatorDefault()
) : Indicator {
    override val id: String = Indicator.SMA
}