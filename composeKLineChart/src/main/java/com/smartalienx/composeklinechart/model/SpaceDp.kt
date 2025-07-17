package com.smartalienx.composeklinechart.model

data class SpaceDp(
    val top: Float,
    val bottom: Float
) {
    companion object {
        fun mainDefault(): SpaceDp {
            return SpaceDp(
                top = 24f,
                bottom = 20f
            )
        }

        fun indicatorDefault(): SpaceDp {
            return SpaceDp(
                top = 24f,
                bottom = 8f
            )
        }
    }
}

fun SpaceDp.toPx(density: Float): Pair<Float, Float> {
    return top * density to bottom * density
}
