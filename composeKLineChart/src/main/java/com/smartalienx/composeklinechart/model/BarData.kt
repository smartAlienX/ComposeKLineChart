package com.smartalienx.composeklinechart.model

import java.math.BigDecimal
import java.time.LocalDateTime

interface BarData {
    val time: Long
    val open: Float
    val high: Float
    val low: Float
    val close: Float
}