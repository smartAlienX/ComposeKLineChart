package com.smartalienx.composeklinechart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smartalienx.composeklinechart.datasource.ChartDataManager
import com.smartalienx.composeklinechart.drawer.CanvasParams
import com.smartalienx.composeklinechart.drawer.IndicatorsDrawerFactory
import com.smartalienx.composeklinechart.drawer.MainChartDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.crosshairs.DefaultCrossHairsDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.TimeAxisDrawer
import com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis.TimeAxisHelper
import com.smartalienx.composeklinechart.drawer.indicatordrawer.IndicatorCanvasDrawer
import com.smartalienx.composeklinechart.drawer.indicatordrawer.MainIndicatorCanvasDrawer
import com.smartalienx.composeklinechart.extension.calculate
import com.smartalienx.composeklinechart.extension.detectTouchGesture
import com.smartalienx.composeklinechart.model.BarData
import com.smartalienx.composeklinechart.model.config.ChartConfig
import com.smartalienx.composeklinechart.model.indicator.Indicator
import com.smartalienx.composeklinechart.model.indicator.IndicatorSeries
import com.smartalienx.composeklinechart.model.indicator.SMAIndicator
import com.smartalienx.composeklinechart.model.indicator.VolumeIndicator
import kotlin.random.Random

@Composable
fun KLineChart(
    modifier: Modifier = Modifier,
    chartConfig: ChartConfig = ChartConfig(),
    dataList: List<BarData>,
    indicators: List<Indicator> = emptyList(),
    indicatorDrawerBuilder: ((indicator: Indicator) -> IndicatorCanvasDrawer<*>)? = null,
    onCrossHairsChange: (Offset?, time: Long?) -> Unit = { _, _ -> }
) {

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var movingDistanceChange by remember { mutableFloatStateOf(0f) }
    var scaleChange by remember { mutableFloatStateOf(1f) }
    var crossHairsPoint by remember { mutableStateOf<Offset?>(null) }

    val canvasParams = rememberCanvasParams(dataList.size)
    val dataManager = rememberChartDataManager(dataList, indicators)
    val indicatorsDrawerFactory = rememberIndicatorsDrawerFactory(indicators, indicatorDrawerBuilder)
    val mainChartDrawer = remember { MainChartDrawer() }
    val timeAxisHelper = remember { TimeAxisHelper() }
    val timeAxisDrawer = remember { TimeAxisDrawer() }
    val crossHairsDrawer = remember { DefaultCrossHairsDrawer() }

    LaunchedEffect(dataList, indicators) {
        dataManager.updateKLineData(dataList)
        dataManager.calculate(indicators)
    }

    LaunchedEffect(indicators) {
        indicatorsDrawerFactory.setupIndicators(indicators)
    }

    LaunchedEffect(crossHairsPoint) {
        val time = crossHairsPoint?.x?.let {
            timeAxisHelper.xToTime(it)
        }
        onCrossHairsChange.invoke(crossHairsPoint, time)
    }

    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
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
                        onLongPress = {
                            crossHairsPoint = it
                        },
                        onLongPressDrag = {
                            val clampedY = it.y.coerceIn(0f, canvasParams.getHeight())
                            crossHairsPoint = it.copy(y = clampedY)
                        },
                        onCancel = {
                            movingDistanceChange = 0f
                            scaleChange = 0f
                            crossHairsPoint = null
                        }
                    )
                }
        ) {

            canvasParams.updateCanvasSize(width = size.width, height = size.height)
            canvasParams.updateParamsChange(dataList.size, movingDistanceChange, scaleChange)
            canvasParams.calculateStartEndIndex()

            timeAxisHelper.calculate(canvasParams, dataList)

            mainChartDrawer.setMainIndicators(indicators.filter { it.isAddToMainChart })

            val subIndicators = indicators.filter { !it.isAddToMainChart }
            val mainIndicators = indicators.filter { it.isAddToMainChart }
            val timeAxisHeight = chartConfig.timeAxis.heightDp * canvasParams.density
            val indicatorsSpace = indicators.sumOf { it.topSpaceDp.toDouble() }.toFloat()
            val mainHeight = (size.height - timeAxisHeight - indicatorsSpace) / (1 + subIndicators.size * chartConfig.subChartScale)
            val subChartHeight = mainHeight * chartConfig.subChartScale

            val contentRect = Rect(left = 0f, top = 0f, right = size.width, bottom = size.height)
            val mainRect = contentRect.copy(bottom = mainHeight)
            val timeAxisRect = mainRect.copy(top = mainRect.bottom, bottom = mainRect.bottom + timeAxisHeight)
            val subChartRect = timeAxisRect.copy(top = timeAxisRect.bottom, bottom = timeAxisRect.bottom + subChartHeight)
            val subChartRectMap = mutableMapOf<Indicator, Rect>()

            subIndicators.forEachIndexed { index, indicator ->
                val topSpace = indicator.topSpaceDp * density.density
                subChartRectMap[indicator] = subChartRect.copy(
                    top = subChartRect.top + topSpace + index * subChartHeight,
                    bottom = subChartRect.bottom + index * subChartHeight
                )
            }

            mainChartDrawer.onPreDraw(mainRect, timeAxisHelper, chartConfig, canvasParams, dataManager)
            timeAxisDrawer.onPreDraw(timeAxisRect, timeAxisHelper, chartConfig, canvasParams, dataManager)

            mainIndicators.forEach { indicator ->
                val indicatorDrawer = indicatorsDrawerFactory.getIndicatorsDrawer(indicator)
                    ?: return@forEach
                if (indicatorDrawer is MainIndicatorCanvasDrawer) {
                    indicatorDrawer.setYAxisValueConversion(mainChartDrawer.yAxisDrawer)
                    indicatorDrawer.onPreDraw(mainRect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
                }
            }

            subIndicators.forEach { indicator ->
                val rect = subChartRectMap[indicator] ?: return@forEach
                indicatorsDrawerFactory.getIndicatorsDrawer(indicator)?.onPreDraw(rect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
            }

            drawIntoCanvas { canvas ->

                mainChartDrawer.onDraw(canvas, mainRect, timeAxisHelper, chartConfig, canvasParams, dataManager)
                timeAxisDrawer.onDraw(canvas, timeAxisRect, timeAxisHelper, chartConfig, canvasParams, dataManager)

                mainIndicators.forEach { indicator ->
                    val indicatorDrawer = indicatorsDrawerFactory.getIndicatorsDrawer(indicator)
                        ?: return@forEach
                    if (indicatorDrawer is MainIndicatorCanvasDrawer) {
                        indicatorDrawer.setYAxisValueConversion(mainChartDrawer.yAxisDrawer)
                        indicatorDrawer.onDraw(canvas, mainRect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
                    }
                }

                subIndicators.forEach { indicator ->
                    val rect = subChartRectMap[indicator] ?: return@forEach
                    indicatorsDrawerFactory.getIndicatorsDrawer(indicator)?.onDraw(canvas, rect, timeAxisHelper, indicator, chartConfig, canvasParams, dataManager)
                }

                // show crossHairs
                val crossHairsY = crossHairsPoint?.y
                if (crossHairsY != null && chartConfig.crossHairs.isShow) {
                    val yAxisValueConversion = if (crossHairsY in mainRect.top..mainRect.bottom) {
                        mainChartDrawer
                    } else {
                        subChartRectMap.entries
                            .firstOrNull { (_, rect) ->
                                crossHairsY in rect.top..rect.bottom
                            }?.key?.let {
                                indicatorsDrawerFactory.getIndicatorsDrawer(it)
                            }
                    }
                    crossHairsDrawer.onDraw(canvas, contentRect, timeAxisRect, crossHairsPoint, timeAxisHelper, yAxisValueConversion, canvasParams, chartConfig)
                }
            }
        }
    }
}

@Composable
private fun rememberCanvasParams(initDataSize: Int = 0): CanvasParams {
    val density = LocalDensity.current.density
    return remember {
        CanvasParams(density).apply {
            updateDataCount(initDataSize)
        }
    }
}

@Composable
private fun rememberChartDataManager(
    initDataList: List<BarData>,
    initIndicators: List<Indicator> = emptyList(),
): ChartDataManager {
    return remember {
        ChartDataManager().apply {
            updateKLineData(initDataList)
            calculate(initIndicators)
        }
    }
}

@Composable
private fun rememberIndicatorsDrawerFactory(
    initIndicators: List<Indicator> = emptyList(),
    indicatorDrawerBuilder: ((indicator: Indicator) -> IndicatorCanvasDrawer<*>)? = null
): IndicatorsDrawerFactory {
    return remember {
        IndicatorsDrawerFactory(indicatorDrawerBuilder).apply {
            setupIndicators(initIndicators)
        }
    }
}

private fun Map<Indicator, Rect>.findByY(y: Float): Indicator? {
    return entries.firstOrNull { (_, rect) ->
        y in rect.top..rect.bottom
    }?.key
}

@Composable
@Preview
fun KLineChartPreview() {
    val sampleData = List(100000) { index ->

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
            .fillMaxSize()
            .padding(vertical = 100.dp, horizontal = 16.dp),
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
            VolumeIndicator(),
            SMAIndicator(
                isAddToMainChart = false,
                series = listOf(
                    IndicatorSeries.SMA(1, 0xFFFF337C.toInt()),
                    IndicatorSeries.SMA(5, 0xFFF69234.toInt()),
                    IndicatorSeries.SMA(10, 0xFF83BD3F.toInt()),
                    IndicatorSeries.SMA(30, 0xFF6200EE.toInt()),
                    IndicatorSeries.SMA(50, 0xFF3FB5BD.toInt()),
                )
            )
        )
    )
}
