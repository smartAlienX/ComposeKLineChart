package com.smartalienx.composeklinechart.drawer

import com.smartalienx.composeklinechart.drawer.indicatordrawer.IndicatorCanvasDrawer
import com.smartalienx.composeklinechart.drawer.indicatordrawer.SMAIndicatorDrawer
import com.smartalienx.composeklinechart.model.indicator.Indicator
import com.smartalienx.composeklinechart.model.indicator.SMAIndicator

class IndicatorsDrawerFactory(
    private val drawerBuilder: ((indicator: Indicator) -> IndicatorCanvasDrawer<*>)? = null
) {

    private val indicatorsDrawerMap = mutableMapOf<Indicator, IndicatorCanvasDrawer<*>>()

    fun setupIndicators(indicators: List<Indicator>) {
        val iterator = indicatorsDrawerMap.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (key !in indicators) {
                iterator.remove()
            }
        }

        indicators.forEach {
            if (indicatorsDrawerMap.containsKey(it)) {
                return@forEach
            }

            var drawer = drawerBuilder?.invoke(it)

            if (drawer == null) {
                drawer = when (it) {
                    // Add other indicators here as needed
                    is SMAIndicator -> SMAIndicatorDrawer()
                    else -> throw IllegalArgumentException("No drawer found for indicator: $it")
                }
            }

            indicatorsDrawerMap[it] = drawer
        }
    }

    fun <I : Indicator> getIndicatorsDrawer(indicator: I): IndicatorCanvasDrawer<I>? {
        return indicatorsDrawerMap[indicator] as IndicatorCanvasDrawer<I>?
    }
}