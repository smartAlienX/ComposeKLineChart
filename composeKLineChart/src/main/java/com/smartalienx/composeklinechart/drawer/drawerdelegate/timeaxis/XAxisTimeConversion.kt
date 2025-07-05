package com.smartalienx.composeklinechart.drawer.drawerdelegate.timeaxis

interface XAxisTimeConversion {

    fun timeToX(time: Long): Float

    fun xToTime(x: Float): Long

    fun indexToX(index: Int): Float

}