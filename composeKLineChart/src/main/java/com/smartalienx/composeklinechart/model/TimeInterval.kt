package com.smartalienx.composeklinechart.model

sealed interface TimeInterval {

    data class Minute(val value: Int) : TimeInterval

    data class Day(val value: Int = 1) : TimeInterval

    data object Week : TimeInterval

    data class Month(val value: Int = 1) : TimeInterval

    data object Year : TimeInterval
}