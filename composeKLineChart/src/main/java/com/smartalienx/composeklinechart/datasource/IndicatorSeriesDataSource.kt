package com.smartalienx.composeklinechart.datasource

import com.smartalienx.composeklinechart.model.indicator.Indicator

abstract class IndicatorSeriesDataSource<S : Indicator.Series, D> : IndicatorSeriesSource<S, D> {

    protected val dataCacheMap = mutableMapOf<String, List<D?>>()

    override fun clear() {
        dataCacheMap.clear()
    }

    override fun getData(series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>> {
        return series.associateWith { s ->
            getData(s, startIndex, endIndex)
        }
    }

    override fun getData(series: List<S>): Map<S, List<D?>> {
        return series.associateWith { s ->
            getData(s)
        }
    }

    override fun getData(series: S, startIndex: Int, endIndex: Int): List<D?> {
        val seriesDataList = dataCacheMap[series.uniqueId()] ?: return emptyList()
        if (seriesDataList.isEmpty()) return emptyList()

        return seriesDataList.subList(
            fromIndex = startIndex.coerceAtLeast(0),
            toIndex = (endIndex + 1).coerceAtMost(seriesDataList.size)
        )
    }

    override fun getData(series: S): List<D?> {
        return dataCacheMap[series.uniqueId()] ?: emptyList()
    }
}