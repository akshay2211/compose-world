package compose.world.composables.knob_picker

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.world.R
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sqrt

@Composable
fun SelectionKnob(
    circleRadius: Dp = 50.dp,
    viewPortWidth: Dp,
    viewPortHeight: Dp,
    onProgressChanged: (Int) -> Unit,
) {
    val density = LocalDensity.current
    // This indicates the center of the circle, most important value to calculation.
    val circleCenter = Offset(
        x = density.run { viewPortWidth.toPx() / 2 },
        y = density.run { viewPortHeight.toPx() / 2 }
    )
    // This is where user is currently touches. Set to center initially (progress: 0)
    var touchCoordinate by remember {
        mutableStateOf(value = Offset(x = circleCenter.x, y = 0F))
    }
    // Save circle radius in px for calculating for once
    val circleRadiusPx = density.run { circleRadius.toPx() }
    // Progress over scale 100.
    var progress by remember { mutableDoubleStateOf(value = 0.0) }

    LaunchedEffect(Unit) {
        snapshotFlow { progress.toInt() }.collect {
            onProgressChanged(progress.toInt())
        }
    }

    Box(
        modifier = Modifier
            .requiredWidth(viewPortWidth)
            .requiredHeight(viewPortHeight)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touchCoordinate = it },
                    onDrag = { _, dragAmount -> touchCoordinate += dragAmount }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .requiredSize(circleRadius * 2)
                .graphicsLayer {
                    // Find `scale` value to scale down/up touch coordinates so that they lie on the circle
                    val nearAdjacentLength = touchCoordinate.x - circleCenter.x
                    val oppositeAdjacentLength = touchCoordinate.y - circleCenter.y
                    val hypotenuse =
                        sqrt(nearAdjacentLength * nearAdjacentLength + oppositeAdjacentLength * oppositeAdjacentLength)
                    val touchTriangleScale = hypotenuse / circleRadiusPx

                    // We need these 2 vector coordinates to calculate degrees between them.
                    val startCoordinateVector = Offset(x = 0F, y = -circleRadiusPx)
                    val touchPointReflectionVector = Offset(
                        x = nearAdjacentLength / touchTriangleScale,
                        y = oppositeAdjacentLength / touchTriangleScale
                    )

                    // Calculate degrees using formula: dot product = len1 * len2 * cos(theta)
                    val dotProduct =
                        startCoordinateVector.x * touchPointReflectionVector.x + startCoordinateVector.y * touchPointReflectionVector.y
                    val radian = acos(dotProduct / (circleRadiusPx * circleRadiusPx)).toDouble()
                    // If user touches on the left of the circle, we need to reverse the angle so the progress keeps increasing
                    val modifiedRadian =
                        if (touchPointReflectionVector.x < 0) (2 * PI - radian) else radian
                    // Progress = modified radian over full circle in degrees (2 PI)
                    progress = (modifiedRadian / (2 * PI)) * 100

                    rotationZ = ((progress / 100) * 360).toFloat()
                },
            painter = painterResource(R.drawable.image_knob),
            contentDescription = "knob"
        )
    }
}