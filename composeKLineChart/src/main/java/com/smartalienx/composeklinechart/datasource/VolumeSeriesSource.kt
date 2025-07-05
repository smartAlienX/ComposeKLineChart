package com.smartalienx.composeklinechart.datasource

import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries

class VolumeSeriesSource : IndicatorSeriesDataSource<IndicatorSeries.Volume, SeriesData>() {

    override fun calculate(seriesList: List<IndicatorSeries.Volume>, dataList: List<BarData>) {

        val activeSeries = seriesList.filterNot { dataCacheMap.containsKey(it.uniqueId()) }
        if (activeSeries.isEmpty()) return

        for (series in activeSeries) {
            val result = mutableListOf<SeriesData?>()

            for (i in dataList.indices) {
                result.add(SeriesData(time = dataList[i].time, value = dataList[i].volume))
            }

            dataCacheMap[series.uniqueId()] = result.map { it }
        }
    }
}