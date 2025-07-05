package com.smartalienx.composeklinechart.extension

import com.smartalienx.composeklinechart.model.indicator.Indicator

fun List<Indicator>.getAllSeries(): Map<String, List<Indicator.Series>> {
    return this.flatMap { indicator ->
        indicator.series.map { series ->
            series.seriesId to series
        }
    }.groupBy({ it.first }, { it.second })
}