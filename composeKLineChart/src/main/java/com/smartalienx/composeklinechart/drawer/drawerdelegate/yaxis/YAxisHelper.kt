package com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis

class YAxisHelper : YAxisValueConversion {

    private var maxValue: Float = 0f
    private var minValue: Float = 0f

    private var topY = 0f
    private var bottomY = 0f

    fun setMinMaxValue(minValue: Float, maxValue: Float) {
        this.minValue = minValue
        this.maxValue = maxValue
    }

    fun setYAxisRange(topY: Float, bottomY: Float) {
        this.topY = topY
        this.bottomY = bottomY
    }

    override fun valueToY(value: Float): Float {
        if (maxValue == minValue) return topY
        val ratio = (value - minValue) / (maxValue - minValue)
        return bottomY - ratio * (bottomY - topY)
    }

    override fun yToValue(y: Float): Float {
        if (maxValue == minValue) return minValue
        val ratio = (y - bottomY) / (topY - bottomY)
        return minValue + ratio * (maxValue - minValue)
    }
}