package com.smartalienx.composeklinechart.datasource

import android.util.Log
import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries

class SMASeriesSource : IndicatorSeriesDataSource<IndicatorSeries.SMA, SeriesData>() {

    override fun calculate(seriesList: List<IndicatorSeries.SMA>, dataList: List<BarData>) {

        var nowTime = System.currentTimeMillis()

        val activeSeries = seriesList.filterNot { dataCacheMap.containsKey(it.uniqueId()) }
        if (activeSeries.isEmpty()) return

        for (series in activeSeries) {
            val period = series.period
            val result = mutableListOf<SeriesData?>()
            var sum = 0f
            for (i in dataList.indices) {
                sum += dataList[i].close
                if (i >= period) {
                    sum -= dataList[i - period].close
                }
                if (i >= period - 1) {
                    result.add(SeriesData(time = dataList[i].time, value = sum / period))
                } else {
                    result.add(null)
                }
            }
            dataCacheMap[series.uniqueId()] = result.map { it }
        }

        Log.d("ChartDataManager", "SMASeriesSource2 calculate time: ${System.currentTimeMillis() - nowTime} ms, seriesList: $seriesList, dataList size: ${dataList.size}")
    }
}