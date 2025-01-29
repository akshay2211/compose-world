package compose.world.composables.dynamic_graph.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

val nodeSize = 80.dp

class Node(
    title: String,
    coordinate: Offset = Offset.Zero
) {
    var title by mutableStateOf(title)
    var coordinate by mutableStateOf(coordinate)
    var color by mutableStateOf(Color.Blue)
    var connectionWidth by mutableFloatStateOf(1f)
    var connections by mutableStateOf(emptyList<NodeConnection>())
}
@Composable
fun NodeWidget(
    node: Node,
    globalRotation: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Node
    Box(
        modifier = Modifier
            .zIndex(100f)
            .size(nodeSize)
            .graphicsLayer {
                translationX = node.coordinate.x
                translationY = node.coordinate.y
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    node.coordinate += dragAmount
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() }
                )
            }
            .background(
                color = node.color,
                shape = CircleShape
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color.Red,
                        shape = CircleShape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -globalRotation
                }
                .padding(24.dp),
            text = node.title,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun NodeWidgetPrev() {
    NodeWidget(
        node = Node("A"),
        isSelected = true,
        globalRotation = 0f,
        onClick = {}
    )
}