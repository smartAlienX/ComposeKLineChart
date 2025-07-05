package com.smartalienx.composeklinechart.drawer.drawerdelegate.charttype

import com.smartalienx.composeklinechart.model.charttype.ChartType

class ChartTypeDrawerFactory {

    private val drawerFactory = mutableMapOf<ChartType, ChartTypeDrawerDelegate>()

    init {
        drawerFactory[ChartType.Candle] = DefaultCandlesDrawer()
    }

    fun registerDrawer(chartType: ChartType, drawer: ChartTypeDrawerDelegate) {
        drawerFactory[chartType] = drawer
    }

    fun getDrawer(chartType: ChartType): ChartTypeDrawerDelegate? {
        return drawerFactory[chartType]
    }
}