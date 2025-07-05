package com.smartalienx.composeklinechart.model.indicator

data class SMAIndicator(
    override val isAddToMainChart: Boolean = true,
    override val series: List<Series>
) : MAIndicator(series) {
    override val id: String = Indicator.SMA
}