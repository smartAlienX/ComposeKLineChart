package com.smartalienx.composeklinechart.extension

import com.smartalienx.composeklinechart.datasource.ChartDataManager
import com.smartalienx.composeklinechart.model.indicator.Indicator

fun ChartDataManager.calculate(indicators: List<Indicator>) {
    this.calculate(indicators.getAllSeries())
}