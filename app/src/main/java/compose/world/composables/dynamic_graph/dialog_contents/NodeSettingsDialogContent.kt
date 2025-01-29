package compose.world.composables.dynamic_graph.dialog_contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.composables.dynamic_graph.components.Node

@Composable
fun NodeSettingsDialogContent(
    selectedNode: Node
) {
    var title by remember (selectedNode) { mutableStateOf(selectedNode.title) }
    var hexCode by remember { mutableStateOf("#") }
    var connectionThickness by remember { mutableStateOf(selectedNode.connectionWidth.toString()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            label = {
                Text("Title")
            },
            onValueChange = {
                title = it
                selectedNode.title = title.subSequence(0, title.length.coerceAtMost(3)).toString()
            }
        )
        TextField(
            value = hexCode,
            label = {
                Text("Color HEX Code")
            },
            onValueChange = {
                hexCode = it
                runCatching {
                    selectedNode.color = Color(android.graphics.Color.parseColor(hexCode))
                }
            }
        )

        TextField(
            value = connectionThickness,
            label = {
                Text("Connection thickness")
            },
            onValueChange = {
                connectionThickness = it
                selectedNode.connectionWidth = (connectionThickness.toFloatOrNull() ?: 1f)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun NodeSettingsDialogPrev() {
    NodeSettingsDialogContent(
        selectedNode = Node("A")
    )
}