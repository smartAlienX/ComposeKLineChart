package com.smartalienx.composeklinechart.extension

import android.util.Log
import androidx.compose.animation.SplineBasedFloatDecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun PointerInputScope.detectDragFlingGesture(
    scope: CoroutineScope,
    density: Density,
    onDrag: (delta: Float) -> Unit,
    onFling: (velocity: Float) -> Unit
) {

    val decay = SplineBasedFloatDecayAnimationSpec(density = density)
    val velocityTracker = VelocityTracker()

    detectDragGestures(
        onDragStart = { offset ->
            velocityTracker.resetTracking()
            velocityTracker.addPosition(
                System.currentTimeMillis(),
                offset
            )
        },
        onDrag = { change, dragAmount ->
            onDrag.invoke(dragAmount.x)
            velocityTracker.addPosition(
                change.uptimeMillis,
                change.position
            )
            change.consume()
        },
        onDragEnd = {
            val velocity = velocityTracker.calculateVelocity().x
            scope.launch {
                var lastValue = 0f
                animateDecay(
                    initialValue = 0f,
                    initialVelocity = velocity,
                    animationSpec = decay
                ) { value, _ ->
                    val delta = value - lastValue
                    lastValue = value
                    onFling.invoke(delta)
                }
            }
        }
    )
}

suspend fun PointerInputScope.detectTouchGesture(
    scope: CoroutineScope,
    density: Density,
    onStart: ((position: Offset) -> Unit)? = null,
    onClick: ((position: Offset) -> Unit)? = null,
    onDrag: ((dragAmount: Offset) -> Unit)? = null,
    onLongPress: ((position: Offset) -> Unit)? = null,
    onLongPressDrag: ((dragAmount: Offset) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onFling: ((velocity: Float) -> Unit)? = null,
    onZoom: ((zoomChange: Float) -> Unit)? = null
) {

    awaitEachGesture {
        var longPressed = false

        val decay = SplineBasedFloatDecayAnimationSpec(density = density)
        val velocityTracker = VelocityTracker()

        var longPressJob: Job? = null

        fun PointerInputChange.isLongPressed(): Boolean {
            return uptimeMillis - previousUptimeMillis > viewConfiguration.longPressTimeoutMillis
        }

        while (true) {
            val pointerEvent = awaitPointerEvent(PointerEventPass.Final)

            // single touch
            if (pointerEvent.changes.size == 1) {
                val pointer = pointerEvent.changes[0]

                // press cancel
                if (pointer.pressed.not()) {

                    longPressJob?.cancel()
                    onCancel?.invoke()

                    val velocity = velocityTracker.calculateVelocity().x
                    scope.launch {
                        var lastValue = 0f
                        animateDecay(
                            initialValue = 0f,
                            initialVelocity = velocity,
                            animationSpec = decay
                        ) { value, _ ->
                            val delta = value - lastValue
                            lastValue = value
                            onFling?.invoke(delta)
                        }
                    }

                    break
                }

                if (pointer.pressed && pointer.previousPressed.not()) { // first press
                    onStart?.invoke(pointer.position)

                    velocityTracker.resetTracking()
                    velocityTracker.addPosition(System.currentTimeMillis(), pointer.position)

                    longPressJob = scope.launch {
                        delay(viewConfiguration.longPressTimeoutMillis)
                        longPressed = true
                        onLongPress?.invoke(pointer.position)
                    }

                } else if (longPressed && pointer.previousPressed) { // long press drag
                    longPressJob?.cancel()
                    onLongPressDrag?.invoke(pointer.position)
                } else if (pointer.previousPressed) { // normal drag
                    longPressJob?.cancel()
                    onDrag?.invoke(pointer.positionChange())

                    velocityTracker.addPosition(
                        pointer.uptimeMillis,
                        pointer.position
                    )
                } else { // single
                    longPressJob?.cancel()
                    onClick?.invoke(pointer.position)
                }
            } else if (pointerEvent.changes.size == 2) {
                onZoom?.invoke(pointerEvent.calculateZoom())
            }
        }
    }

}


