package compose.world.composables.dynamic_graph.layers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.composables.dynamic_graph.components.nodeSize

class GridLayerState {
    var gridThickness by mutableFloatStateOf(1f)
    var cellSize by mutableStateOf(nodeSize)
}

@Composable
fun GridLayer(
    state: GridLayerState,
    globalScale: Float,
    globalOffset: Offset
) {
    val config = LocalConfiguration.current
    Canvas(
        modifier = Modifier.fillMaxSize()
//            .graphicsLayer {
//                translationX = globalOffset.x
//                translationY = globalOffset.y
//            }
    ) {
        // Grid lines in x axis
        for (i in 0..size.width.toInt() step (state.cellSize * globalScale).toPx().toInt()) {
            drawLine(
                color = Color.Gray,
                start = Offset(i.toFloat(), 0f),
                end = Offset(i.toFloat(), size.height),
                strokeWidth = state.gridThickness.dp.toPx()
            )
        }
        // Grid lines in y axis
        for (i in 0..size.height.toInt() step (state.cellSize * globalScale).toPx().toInt()) {
            drawLine(
                color = Color.Gray,
                start = Offset(0f, i.toFloat()),
                end = Offset(size.width, i.toFloat()),
                strokeWidth = state.gridThickness.dp.toPx()
            )
        }
    }
}

@Preview
@Composable
private fun GridLayerPrev() {
    GridLayer(
        state = GridLayerState(),
        globalScale = 1f,
        globalOffset = Offset.Zero
    )
}