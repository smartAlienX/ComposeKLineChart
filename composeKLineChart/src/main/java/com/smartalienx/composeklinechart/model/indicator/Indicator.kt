package com.smartalienx.composeklinechart.model.indicator

import com.smartalienx.composeklinechart.model.config.ChartConfig

interface Indicator {

    val id: String

    val isAddToMainChart: Boolean

    val series: List<Series>

    val yAxisConfig: ChartConfig.YAxis

    interface Series {
        val seriesId: String
        fun paramsCode(): String

        fun uniqueId(): String {
            return "$seriesId-${paramsCode()}"
        }

        companion object {
            const val SMA = "SMA"
        }
    }

    companion object {
        const val SMA = "SMA-Indicator"
    }
}