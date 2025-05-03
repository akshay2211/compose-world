package compose.world.composables.scaling_dots

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun ScalingDots() {
    Box(modifier = Modifier.fillMaxSize()) {

        var lastTouchedPointX by remember { mutableStateOf<Float?>(0F) }
        var lastTouchedPointY by remember { mutableStateOf<Float?>(0F) }

        Canvas(
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            lastTouchedPointX = it.x
                            lastTouchedPointY = it.y
                        },
                        onDragEnd = {
                            lastTouchedPointX = null
                            lastTouchedPointY = null
                        },
                        onDrag = { _, drag->
                            val newLastTouchedPointX = drag.x + lastTouchedPointX!!
                            val newLastTouchedPointY = drag.y + lastTouchedPointY!!

                            lastTouchedPointX = newLastTouchedPointX
                            lastTouchedPointY = newLastTouchedPointY
                        }
                    )
                }
        ) {
            val circleRadius = 8.dp.toPx()
            val width = size.width
            val height = size.height
            val padding = 24.dp.toPx()

            val horizontalCircleCount = (width / (circleRadius * 2 + padding)).roundToInt() + 1
            val verticalCircleCount = (height / (circleRadius * 2 + padding)).roundToInt() + 1

            repeat(verticalCircleCount) { vi ->
                repeat(horizontalCircleCount) { hi ->
                    val center = Offset(
                        y = (circleRadius * 2 + padding) * vi + circleRadius,
                        x = (circleRadius * 2 + padding) * hi + circleRadius * 2F
                    )

                    val distanceFromTouchX = abs(center.x - (lastTouchedPointX ?: 0F))
                    val distanceFromTouchY = abs(center.y - (lastTouchedPointY ?: 0F))
                    val distanceFromTouch = if (lastTouchedPointX == null || lastTouchedPointY == null) Float.MAX_VALUE
                                            else sqrt(distanceFromTouchX.pow(2) + distanceFromTouchY.pow(2))

                    val radius = circleRadius + (circleRadius * 2 - distanceFromTouch / 6 ).coerceAtLeast(0F)
                    val greenValue = ((255F - abs(distanceFromTouch)) / 255).coerceAtLeast(0F)

                    drawCircle(
                        color = Color(red = greenValue / 2, green = greenValue, blue = greenValue / 2),
                        radius = radius,
                        center = center
                    )
                }
            }
        }

    }
}

@Preview (showBackground = true)
@Composable
private fun ScalingDotsPrev() {
    ScalingDots()
}