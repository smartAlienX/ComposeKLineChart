//package com.smartalienx.composeklinechart.model.indicator
//
//import com.smartalienx.composeklinechart.model.config.ChartConfig
//import com.smartalienx.composeklinechart.model.indicator.MAIndicator.Series
//
//class VolumeIndicator(
//    override val series: List<Series> = listOf(),
//    override val yAxisConfig: ChartConfig.YAxis = ChartConfig.YAxis(count = 3)
//) : Indicator {
//    override val id: String = Indicator.VOLUME
//    override val isAddToMainChart: Boolean = false
//
//    data class Series()
//}