package com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis

import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.indexRange
import com.smartalienx.composeklinechart.model.BarData
import kotlin.math.abs

class TimeAxisHelper : XAxisTimeConversion {

    private val timeToXMap = mutableMapOf<Long, Float>()
    private val xToTimeMap = mutableMapOf<Float, Long>()

    private var cachedStartX = 0f
    private var cachedCandleUnit = 0f
    private var cachedIndexStart = 0

    fun calculate(canvasParams: CanvasParams, dataList: List<BarData>) {
        timeToXMap.clear()
        xToTimeMap.clear()

        val candleUnit = canvasParams.getCandleUnitWithScale()

        cachedStartX = canvasParams.getStartX()
        cachedCandleUnit = candleUnit
        cachedIndexStart = canvasParams.getIndexStart()

        var x = cachedStartX

        for (i in canvasParams.indexRange) {
            if (i !in dataList.indices) continue
            val time = dataList[i].time
            timeToXMap[time] = x + candleUnit / 2
            xToTimeMap[x] = time
            x += candleUnit
        }
    }

    override fun timeToX(time: Long): Float {
        return timeToXMap[time] ?: 0f
    }

    override fun xToTime(x: Float): Long {
        return xToTimeMap.entries.minByOrNull { (key, _) -> abs(x - key) }?.value ?: 0L
    }

    override fun indexToX(index: Int): Float {
        val indexStart = cachedIndexStart
        return cachedStartX + (index - indexStart) * cachedCandleUnit + cachedCandleUnit / 2
    }
}