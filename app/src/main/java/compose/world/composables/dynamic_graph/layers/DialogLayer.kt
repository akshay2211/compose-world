package compose.world.composables.dynamic_graph.layers

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DialogLayer(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: (@Composable () -> Unit)
) {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    if (isVisible) {
        var offsetX by remember { mutableFloatStateOf(density.run { 0f }) }
        var offsetY by remember { mutableFloatStateOf(density.run { config.screenHeightDp.dp.toPx() / 2 }) }
        Box(modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY
            }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onDismiss()
            }
            .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun DialogLayerPrev() {
    DialogLayer(
        isVisible = false,
        onDismiss = {}
    ) {
        Text("Hi!")
    }
}