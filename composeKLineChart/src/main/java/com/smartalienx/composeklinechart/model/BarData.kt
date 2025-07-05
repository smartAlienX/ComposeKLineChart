package com.smartalienx.composeklinechart.model

interface BarData {
    val time: Long
    val open: Float
    val high: Float
    val low: Float
    val close: Float
    val volume: Float
    val turnover: Float
}