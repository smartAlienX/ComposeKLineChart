package com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis

interface YAxisValueConversion {

    fun valueToY(value: Float): Float

    fun yToValue(y: Float): Float

}