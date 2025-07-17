package com.smartalienx.composeklinechart.extension

import androidx.compose.ui.geometry.Rect
import com.smartalienx.composeklinechart.drawer.drawerdelegate.yaxis.YAxisDrawerDelegate
import com.smartalienx.composeklinechart.model.SpaceDp
import com.smartalienx.composeklinechart.model.toPx

fun YAxisDrawerDelegate.setYAxisRangeWithSpace(rect: Rect, spaceDp: SpaceDp, density: Float) {
    val (topSpace, bottomSpace) = spaceDp.toPx(density)
    setYAxisRange(rect.top + topSpace, rect.bottom - bottomSpace)
}