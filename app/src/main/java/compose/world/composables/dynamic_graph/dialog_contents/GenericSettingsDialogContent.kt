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
import compose.world.composables.dynamic_graph.layers.GridLayerState

@Composable
fun GenericSettingsDialogContent(
    gridLayerState: GridLayerState,
) {
    var gridThicknessValue by remember { mutableStateOf(gridLayerState.gridThickness.toString()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = gridThicknessValue,
            label = {
                Text("Grid thickness")
            },
            onValueChange = {
                gridThicknessValue = it
                gridLayerState.gridThickness = it.toFloatOrNull() ?: 1f
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
private fun Prev() {
    GenericSettingsDialogContent(
        gridLayerState = remember { GridLayerState() }
    )
}