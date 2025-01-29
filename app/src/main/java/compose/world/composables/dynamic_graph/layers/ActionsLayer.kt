package compose.world.composables.dynamic_graph.layers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.composables.dynamic_graph.GraphScreenState

@Composable
fun ActionsLayer(
    screenState: GraphScreenState,
    onOpenAddNodeDialog: () -> Unit = {},
    onOpenGeneralSettingsDialog: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (screenState.globalOffset != Offset.Zero || screenState.globalRotation != 0f || screenState.globalScale != 1f) {
            ElevatedButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    screenState.globalOffset = Offset.Zero
                    screenState.globalRotation = 0f
                    screenState.globalScale = 1f
                }
            ) {
                Text("Reset")
            }
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(24.dp),
            onClick = onOpenAddNodeDialog
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onOpenGeneralSettingsDialog) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "settings")
        }
    }
}

@Preview
@Composable
private fun ActionsLayerPrev() {
    ActionsLayer(
        screenState = GraphScreenState()
    )
}