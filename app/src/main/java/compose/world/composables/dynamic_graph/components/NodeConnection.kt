package compose.world.composables.dynamic_graph.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NodeConnection (
    val parentNode: Node?,
    val connectedNode: Node?
) {
    var connectionColor by mutableStateOf(Color.Red)
    var connectionThickness by mutableFloatStateOf(1f)
    var connectionWeight by mutableIntStateOf(1)
}

@Composable
fun NodeConnectionWidget(
    connection: NodeConnection,
    connectionLength: Dp,
    offset: IntOffset,
    angle: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
        val textMeasurer = rememberTextMeasurer()
        Canvas(modifier = Modifier
            .size(width = connection.connectionThickness.dp, height = connectionLength)
            .offset {
                offset
            }
            .graphicsLayer {
                rotationZ = angle
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .background(Color.Green)
            .then(if (isSelected) Modifier.border(width = 2.dp, color = Color.Black) else Modifier)
            .clickable { onClick() },
        ) {
            val textLayoutResult = textMeasurer.measure(
                text = "${connection.connectionWeight}",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            rotate(degrees = 90f) {
                translate(
                    top = (size.height / 2) - (textLayoutResult.size.height) - 6.dp.toPx(),
                    left = size.width / 2f - (textLayoutResult.size.width / 2f)
                ) {
                    drawText(
                        textLayoutResult = textLayoutResult
                    )
//                    drawText(
//                        textMeasurer = textMeasurer,
//                        text = "${connection.connectionWeight}",
//                        style = TextStyle(color = Color.Black, fontSize = 32.sp)
//                    )
                }
            }
        }
}

@Preview (showBackground = true)
@Composable
private fun NodeConnectionPrev() {
    NodeConnectionWidget(
        connection = NodeConnection(
            connectedNode = Node(
                title = "Node 2"
            ),
            parentNode = Node(
                title = "Node 1"
            )
        ),
        connectionLength = 50.dp,
        offset = IntOffset.Zero,
        angle = 0f,
        isSelected = true,
        onClick = {}
    )
}