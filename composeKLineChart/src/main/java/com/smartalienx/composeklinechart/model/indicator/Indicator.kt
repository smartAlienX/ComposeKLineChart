package com.smartalienx.composeklinechart.model.indicator

import com.smartalienx.composeklinechart.model.config.ChartConfig

interface Indicator {

    val id: String

    val isAddToMainChart: Boolean

    val series: List<IndicatorSeries>

    val yAxisConfig: ChartConfig.YAxis

    companion object {
        const val SMA = "Indicator-SMA"
        const val VOLUME = "Indicator-VOL"
    }
}

interface IndicatorSeries {

    fun paramsCode(): String

    fun uniqueId(): String {
        return "${this::class.simpleName}-${paramsCode()}"
    }

    data class SMA(
        override val period: Int,
        override val color: Int,
        override val lineWidthDp: Float = 1F
    ) : MA(period, color, lineWidthDp)



    abstract class MA(
        open val period: Int,
        open val color: Int,
        open val lineWidthDp: Float = 1F
    ) : IndicatorSeries {
        override fun paramsCode(): String {
            return period.toString()
        }
    }
}