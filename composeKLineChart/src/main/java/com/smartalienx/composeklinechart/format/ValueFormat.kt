package com.smartalienx.composeklinechart.format

import kotlin.math.round

interface ValueFormat {

    fun formatValue(value: Float): String

}

class DefaultValueFormat : ValueFormat {

    override fun formatValue(value: Float): String {
        val rounded = round(value * 100) / 100
        return String.format("%.2f", rounded)
    }

}