package com.smartalienx.composeklinechart.datasource

import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.indicator.Indicator

class ChartDataManager : KLineDataSource {

    private var kLineData: List<BarData> = emptyList()
    private val seriesDataSourceMap = mutableMapOf<String, IndicatorSeriesSource<*, *>>()

    init {
        registerSource(Indicator.Series.SMA, SMASeriesSource())
    }

    fun registerSource(seriesId: String, seriesDataSource: IndicatorSeriesSource<*, *>) {
        seriesDataSourceMap[seriesId] = seriesDataSource
    }

    fun updateKLineData(kLineData: List<BarData>) {
        if (this.kLineData == kLineData) return
        this.kLineData = kLineData
        seriesDataSourceMap.values.forEach { it.clear() }
    }

    fun calculate(seriesId: String, seriesList: List<Indicator.Series>) {
        val dataSource = getIndicatorDataSource<Indicator.Series, Any>(seriesId)
        if (dataSource != null) {
            dataSource.calculate(seriesList, kLineData)
        } else {
            throw IllegalArgumentException("No data source registered for seriesId: $seriesId")
        }
    }

    override fun getKLineData(): List<BarData> {
        return kLineData
    }

    override fun getKLineData(startIndex: Int, endIndex: Int): List<BarData> {
        return kLineData.subList(startIndex, endIndex)
    }

    override fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: S): List<D?>? {
        return getIndicatorDataSource<S, D>(seriesId)?.getData(series)
    }

    override fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: S, startIndex: Int, endIndex: Int): List<D?>? {
        return getIndicatorDataSource<S, D>(seriesId)?.getData(series, startIndex, endIndex)
    }

    override fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: List<S>): Map<S, List<D?>>? {
        return getIndicatorDataSource<S, D>(seriesId)?.getData(series)
    }

    override fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>>? {
        return getIndicatorDataSource<S, D>(seriesId)?.getData(series, startIndex, endIndex)
    }

    private fun <S : Indicator.Series, D> getIndicatorDataSource(seriesId: String): IndicatorSeriesSource<S, D>? {
        @Suppress("UNCHECKED_CAST")
        return seriesDataSourceMap[seriesId] as? IndicatorSeriesSource<S, D>
    }
}

interface KLineDataSource {

    fun getKLineData(): List<BarData>

    fun getKLineData(startIndex: Int, endIndex: Int): List<BarData>

    fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: S): List<D?>?

    fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: S, startIndex: Int, endIndex: Int): List<D?>?

    fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: List<S>): Map<S, List<D?>>?

    fun <S : Indicator.Series, D> getSeriesData(seriesId: String, series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>>?
}

interface IndicatorSeriesSource<S : Indicator.Series, D> {

    fun calculate(seriesList: List<S>, dataList: List<BarData>)

    fun clear()

    fun getData(series: S): List<D?>

    fun getData(series: S, startIndex: Int, endIndex: Int): List<D?>

    fun getData(series: List<S>): Map<S, List<D?>>

    fun getData(series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>>
}
