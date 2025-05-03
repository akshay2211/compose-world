package compose.world.composables.goofy_onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun GoofyOnboarding() {
    var topPaddingPercentage by remember { mutableFloatStateOf(0.5F) }
    Box(modifier = Modifier.fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                topPaddingPercentage -= dragAmount.y / 1000
            }
        }
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer {
                    translationX = ((topPaddingPercentage - 0.5) * 100).dp.toPx()
                    translationY = -((topPaddingPercentage - 0.5) * 100).dp.toPx()
                }
                .background(color = Color.Blue, shape = CircleShape)
                .align { size, space, layoutDirection ->
                    IntOffset((space.width - size.width) / 2, (space.height * 0.1F).toInt())
                }
        )


        Box(
            modifier = Modifier
                .size(70.dp)
                .graphicsLayer {
                    rotationZ = topPaddingPercentage * 360
                }
                .background(color = Color.Black)
                .align { size, space, layoutDirection ->
                    IntOffset(50, (space.height * topPaddingPercentage - size.height).toInt())
                }
        )

        Canvas(
            modifier = Modifier
                .size(70.dp)
                .graphicsLayer {
                    rotationZ = topPaddingPercentage * 360
                }
//                .background(color = Color.Black, shape = CircleShape)
                .align { size, space, layoutDirection ->
                    IntOffset(space.width - size.width - 50, (space.height * topPaddingPercentage * 1.5 - size.height).toInt())
                }
        ) {
            drawPath(
                path = Path().apply {
                    moveTo(size.width / 2, 0F)
                    lineTo(size.width, size.height)
                    lineTo(0F, size.height)
                    close()
                },
                color = Color.Red
            )
        }

    }
}

@Preview (showBackground = true)
@Composable
private fun GoofyOnboardingPrev() {
    GoofyOnboarding()
}