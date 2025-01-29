package compose.world.composables.dynamic_graph.dialog_contents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import compose.world.composables.dynamic_graph.components.Node
import compose.world.composables.dynamic_graph.components.NodeConnection

@Composable
fun NodeConnectionSettingsDialogContent(
    selectedConnection: NodeConnection,
    onRemoveConnection: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        var hexCode by remember {
            mutableStateOf("#")
        }
        var thickness by remember (selectedConnection) {
            mutableStateOf("${selectedConnection.connectionThickness}")
        }
        var weight by remember (selectedConnection) {
            mutableStateOf("${selectedConnection.connectionWeight}")
        }
        Row {
            TextField(
                modifier = Modifier.weight(1f),
                value = hexCode,
                label = {
                    Text("Connection color")
                },
                onValueChange = {
                    hexCode = it
                    runCatching {
                        selectedConnection.connectionColor = Color(android.graphics.Color.parseColor(hexCode))
                    }
                }
            )
            TextField(
                modifier = Modifier.weight(1f),
                value = thickness,
                label = {
                    Text("Connection thickness")
                },
                onValueChange = {
                    thickness = it
                    selectedConnection.connectionThickness = it.toFloatOrNull() ?: 1f
                }
            )
        }
        Row {
            TextField(
                modifier = Modifier.weight(1f),
                value = weight,
                label = {
                    Text("Connection weight")
                },
                onValueChange = {
                    weight = it
                    selectedConnection.connectionWeight = it.toIntOrNull() ?: 1
                }
            )
            Icon(
                modifier = Modifier.clickable(onClick = onRemoveConnection),
                imageVector = Icons.Default.Clear,
                contentDescription = "discard",
                tint = Color.Red
            )
        }
    }
}

@Preview
@Composable
private fun NodeConnectionSettingsDialogContentPrev() {
    NodeConnectionSettingsDialogContent(
        selectedConnection = NodeConnection(
            connectedNode = Node("X"),
            parentNode = Node("Y")
        ),
        onRemoveConnection = {}
    )
}