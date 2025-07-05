package com.smartalienx.composeklinechart.extension

import com.smartalienx.composeklinechart.model.indicator.Indicator
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries

fun List<Indicator>.getAllSeries(): List<IndicatorSeries> {
    return this.flatMap { indicator ->
        indicator.series
    }
}