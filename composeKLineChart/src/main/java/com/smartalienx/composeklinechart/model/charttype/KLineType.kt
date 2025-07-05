package com.smartalienx.composeklinechart.model.charttype

sealed interface ChartType {
    data object Line : ChartType
    data object Candle : ChartType
}

