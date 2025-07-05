package com.smartalienx.composeklinechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartalienx.composeklinechart.datasource.ChartDataManager
import com.smartalienx.composeklinechart.drawer.CanvasDrawer
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.IndicatorsDrawerFactory
import com.smartalienx.composeklinechart.drawer.MainChartDrawer
import com.smartalienx.composeklinechart.drawer.TimeAxisDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.TimeAxisHelper
import com.smartalienx.composeklinechart.drawer.indicatordrawer.IndicatorCanvasDrawer
import com.smartalienx.composeklinechart.drawer.indicatordrawer.MainIndicatorCanvasDrawer
import com.smartalienx.composeklinechart.drawer.indicatordrawer.SMAIndicatorDrawer
import com.smartalienx.composeklinechart.extension.calculate
import com.smartalienx.composeklinechart.extension.detectDragFlingGesture
import com.smartalienx.composeklinechart.extension.detectTouchGesture
import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.TimeInterval
import com.smartalienx.composeklinechart.model.charttype.ChartType
import com.smartalienx.composeklinechart.model.config.ChartConfig
import com.smartalienx.composeklinechart.model.indicator.Indicator
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries
import com.smartalienx.composeklinechart.model.indicator.SMAIndicator
import com.smartalienx.composeklinechart.model.indicator.VolumeIndicator
import kotlin.random.Random

@Composable
fun KLineChart(
    modifier: Modifier = Modifier,
    chartType: ChartType = ChartType.Candle,
    timeInterval: TimeInterval = TimeInterval.Minute(1),
    chartConfig: ChartConfig = ChartConfig(),
    dataList: List<BarData>,
    indicators: List<Indicator> = emptyList(),
    indicatorDrawerBuilder: ((indicator: Indicator) -> IndicatorCanvasDrawer<*>)? = null
) {

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val canvasParams = remember {
        CanvasParams(density.density).apply {
            updateDataCount(dataList.size)
        }
    }
    var movingDistanceChange by remember { mutableFloatStateOf(0f) }
    var scaleChange by remember { mutableFloatStateOf(1f) }

    val dataManager = remember {
        ChartDataManager().apply {
            updateKLineData(dataList)
            calculate(indicators)
        }
    }
    val indicatorsDrawerFactory = remember {
        IndicatorsDrawerFactory(indicatorDrawerBuilder).apply {
            setupIndicators(indicators)
        }
    }
    val mainChartDrawer = remember { MainChartDrawer() }
    val timeAxisHelper = remember { TimeAxisHelper() }
    val timeAxisDrawer = remember { TimeAxisDrawer() }

    LaunchedEffect(dataList, indicators) {
        dataManager.updateKLineData(dataList)
        dataManager.calculate(indicators)
    }

    LaunchedEffect(indicators) {
        indicatorsDrawerFactory.setupIndicators(indicators)
    }

    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTouchGesture(
                        scope = coroutineScope,
                        density = density,
                        onDrag = {
                            movingDistanceChange = it.x
                        },
                        onFling = { velocity ->
                            movingDistanceChange = velocity
                        },
                        onZoom = { zoom ->
                            scaleChange = zoom
                        },
                        onCancel = {
                            movingDistanceChange = 0f
                            scaleChange = 0f
                        }
                    )
                }
        ) {

            canvasParams.updateCanvasSize(width = size.width, height = size.height)
            canvasParams.updateParamsChange(dataList.size, movingDistanceChange, scaleChange)
            canvasParams.calculateStartEndIndex()

            timeAxisHelper.calculate(canvasParams, dataList)

            mainChartDrawer.setChartType(chartType)
            mainChartDrawer.setMainIndicators(indicators.filter { it.isAddToMainChart })

            val subCharts = indicators.filter { !it.isAddToMainChart }
            val timeAxisHeight = chartConfig.timeAxis.heightDp * canvasParams.density
            val mainHeight = (size.height - timeAxisHeight) / (1 + subCharts.size * chartConfig.subChartScale)
            val subChartHeight = mainHeight * chartConfig.subChartScale

            val mainRect = Rect(left = 0f, top = 0f, right = size.width, bottom = mainHeight)
            val timeAxisRect = mainRect.copy(top = mainRect.bottom, bottom = mainRect.bottom + timeAxisHeight)
            val subChartRect = timeAxisRect.copy(top = timeAxisRect.bottom, bottom = timeAxisRect.bottom + subChartHeight)
            val subChartRectMap = mutableMapOf<Indicator, Rect>()
            indicators.filter { it.isAddToMainChart.not() }.forEachIndexed { index, indicator ->
                subChartRectMap[indicator] = subChartRect.copy(
                    top = subChartRect.top + index * subChartHeight,
                    bottom = subChartRect.bottom + index * subChartHeight
                )
            }

            mainChartDrawer.onPreDraw(mainRect, timeAxisHelper, chartConfig, canvasParams, dataManager)
            timeAxisDrawer.onPreDraw(timeAxisRect, timeAxisHelper, chartConfig, canvasParams, dataManager)

            indicators.filter { it.isAddToMainChart }.forEach { indicator ->
                val indicatorDrawer = indicatorsDrawerFactory.getIndicatorsDrawer(indicator)
                    ?: return@forEach
                if (indicatorDrawer is MainIndicatorCanvasDrawer) {
                    indicatorDrawer.setYAxisValueConversion(mainChartDrawer.yAxisDrawer)
                    indicatorDrawer.onPreDraw(mainRect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
                }
            }

            indicators.filter { it.isAddToMainChart.not() }.forEach { indicator ->
                val rect = subChartRectMap[indicator] ?: return@forEach
                indicatorsDrawerFactory.getIndicatorsDrawer(indicator)?.onPreDraw(rect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
            }

            drawIntoCanvas { canvas ->

                mainChartDrawer.onDraw(canvas, mainRect, timeAxisHelper, chartConfig, canvasParams, dataManager)
                timeAxisDrawer.onDraw(canvas, timeAxisRect, timeAxisHelper, chartConfig, canvasParams, dataManager)

                indicators.filter { it.isAddToMainChart }.forEach { indicator ->
                    val indicatorDrawer = indicatorsDrawerFactory.getIndicatorsDrawer(indicator)
                        ?: return@forEach
                    if (indicatorDrawer is MainIndicatorCanvasDrawer) {
                        indicatorDrawer.setYAxisValueConversion(mainChartDrawer.yAxisDrawer)
                        indicatorDrawer.onDraw(canvas, mainRect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
                    }
                }

                indicators.filter { it.isAddToMainChart.not() }.forEach { indicator ->
                    val rect = subChartRectMap[indicator] ?: return@forEach
                    indicatorsDrawerFactory.getIndicatorsDrawer(indicator)?.onDraw(canvas, rect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
                }
            }
        }
    }
}

@Composable
@Preview
fun KLineChartPreview() {
    val sampleData = List(100) { index ->

        val high = Random.nextInt(100) + 50

        object : BarData {
            override val time: Long = System.currentTimeMillis() - index * 60 * 1000
            override val open: Float = high - Random.nextInt(0, 49).toFloat()
            override val high: Float = high * 1f
            override val low: Float = high - 50f
            override val close: Float = high - Random.nextInt(0, 49).toFloat()
            override val volume: Float = Random.nextInt(100, 1000).toFloat()
            override val turnover: Float = Random.nextInt(1000, 10000).toFloat()
        }
    }.sortedBy { it.time }
    KLineChart(
        modifier = Modifier
            .padding(vertical = 100.dp)
            .fillMaxSize(),
        dataList = sampleData,
        chartConfig = ChartConfig(),
        indicators = listOf(
            SMAIndicator(
                isAddToMainChart = true,
                series = listOf(
                    IndicatorSeries.SMA(1, 0xFFFF337C.toInt()),
                    IndicatorSeries.SMA(5, 0xFFF69234.toInt()),
                    IndicatorSeries.SMA(10, 0xFF83BD3F.toInt()),
                    IndicatorSeries.SMA(30, 0xFF6200EE.toInt()),
                    IndicatorSeries.SMA(50, 0xFF3FB5BD.toInt()),
                )
            ),
            VolumeIndicator()
        )
    )
}
