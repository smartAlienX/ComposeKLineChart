package com.smartalienx.composeklinechart.datasource

import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries
import kotlin.reflect.KClass

class ChartDataManager : KLineDataSource {

    private var kLineData: List<BarData> = emptyList()
    private val seriesDataSourceMap = mutableMapOf<KClass<out IndicatorSeries>, IndicatorSeriesSource<*, *>>()

    init {
        registerSource(IndicatorSeries.SMA::class, SMASeriesSource())
    }

    fun <S : IndicatorSeries> registerSource(seriesClass: KClass<S>, seriesDataSource: IndicatorSeriesSource<*, *>) {
        seriesDataSourceMap[seriesClass] = seriesDataSource
    }

    fun updateKLineData(kLineData: List<BarData>) {
        if (this.kLineData == kLineData) return
        this.kLineData = kLineData
        seriesDataSourceMap.values.forEach { it.clear() }
    }

    fun calculate(seriesList: List<IndicatorSeries>) {
        val seriesMap = seriesList.groupBy { it::class }
        seriesMap.forEach { (seriesKey, series) ->
            getIndicatorDataSource<IndicatorSeries, Any>(seriesKey)?.calculate(series, kLineData)
        }
    }

    override fun getKLineData(): List<BarData> {
        return kLineData
    }

    override fun getKLineData(startIndex: Int, endIndex: Int): List<BarData> {
        return kLineData.subList(startIndex, endIndex)
    }

    override fun <S : IndicatorSeries, D> getSeriesData(series: S): List<D?>? {
        return getIndicatorDataSource<S, D>(series::class)?.getData(series)
    }

    override fun <S : IndicatorSeries, D> getSeriesData(series: S, startIndex: Int, endIndex: Int): List<D?>? {
        return getIndicatorDataSource<S, D>(series::class)?.getData(series, startIndex, endIndex)
    }

    override fun <S : IndicatorSeries, D> getSeriesData(series: List<S>): Map<S, List<D?>>? {
        val seriesDataMap = mutableMapOf<S, List<D?>>()
        val seriesMap = series.groupBy { it::class }
        seriesMap.forEach { (seriesKey, series) ->
            getIndicatorDataSource<S, D>(seriesKey)?.getData(series)?.also {
                seriesDataMap.putAll(it)
            }
        }
        return seriesDataMap
    }

    override fun <S : IndicatorSeries, D> getSeriesData(series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>>? {
        val seriesDataMap = mutableMapOf<S, List<D?>>()
        val seriesMap = series.groupBy { it::class }
        seriesMap.forEach { (seriesKey, series) ->
            getIndicatorDataSource<S, D>(seriesKey)?.getData(series, startIndex, endIndex)?.also {
                seriesDataMap.putAll(it)
            }
        }

        return seriesDataMap
    }

    private fun <S : IndicatorSeries, D> getIndicatorDataSource(seriesId: KClass<out S>): IndicatorSeriesSource<S, D>? {
        @Suppress("UNCHECKED_CAST")
        return seriesDataSourceMap[seriesId] as? IndicatorSeriesSource<S, D>
    }
}

interface KLineDataSource {

    fun getKLineData(): List<BarData>

    fun getKLineData(startIndex: Int, endIndex: Int): List<BarData>

    fun <S : IndicatorSeries, D> getSeriesData(series: S): List<D?>?

    fun <S : IndicatorSeries, D> getSeriesData(series: S, startIndex: Int, endIndex: Int): List<D?>?

    fun <S : IndicatorSeries, D> getSeriesData(series: List<S>): Map<S, List<D?>>?

    fun <S : IndicatorSeries, D> getSeriesData(series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>>?
}

interface IndicatorSeriesSource<S : IndicatorSeries, D> {

    fun calculate(seriesList: List<S>, dataList: List<BarData>)

    fun clear()

    fun getData(series: S): List<D?>

    fun getData(series: S, startIndex: Int, endIndex: Int): List<D?>

    fun getData(series: List<S>): Map<S, List<D?>>

    fun getData(series: List<S>, startIndex: Int, endIndex: Int): Map<S, List<D?>>
}
