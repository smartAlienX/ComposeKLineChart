package com.smartalienx.composeklinechart.extension

import android.util.Log
import com.smartalienx.composeklinechart.datasource.ChartDataManager
import com.smartalienx.composeklinechart.datasource.KLineDataSource
import com.smartalienx.composeklinechart.model.indicator.Indicator

fun ChartDataManager.calculate(indicators: List<Indicator>) {
    indicators.getAllSeries().forEach { (seriesId, seriesList) ->
        this.calculate(seriesId, seriesList)
    }
}