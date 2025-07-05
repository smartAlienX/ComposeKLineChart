package com.smartalienx.composeklinechart.model.indicator

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

    class Volume : IndicatorSeries {
        override fun paramsCode(): String {
            return "Volume"
        }
    }

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