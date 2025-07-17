package com.smartalienx.composeklinechart.model.indicator

import com.smartalienx.composeklinechart.model.SpaceDp
import com.smartalienx.composeklinechart.model.config.ChartConfig

interface Indicator {

    val id: String

    val isAddToMainChart: Boolean

    val series: List<IndicatorSeries>

    val yAxisConfig: ChartConfig.YAxis

    val spaceDp: SpaceDp

    companion object {
        const val SMA = "Indicator-SMA"
        const val VOLUME = "Indicator-VOL"
    }
}

