package com.smartalienx.composeklinechart.format

import android.annotation.SuppressLint
import com.smartalienx.composeklinechart.model.TimeInterval
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

interface TimeFormat {

    val timeZone: TimeZone

    fun format(timeInterval: TimeInterval, timestamp: Long): String

}

class DefaultTimeFormat(override val timeZone: TimeZone) : TimeFormat {

    @SuppressLint("SimpleDateFormat")
    override fun format(timeInterval: TimeInterval, timestamp: Long): String {

        val pattern = when (timeInterval) {
            is TimeInterval.Minute -> "MM-dd HH:mm"
            is TimeInterval.Day,
            is TimeInterval.Week -> "yyyy-MM-dd"

            is TimeInterval.Month -> "yyyy-MM"
            is TimeInterval.Year -> "yyyy"
        }

        val formatter = SimpleDateFormat(pattern).apply {
            timeZone = this@DefaultTimeFormat.timeZone
        }

        return formatter.format(Date(timestamp))
    }
}