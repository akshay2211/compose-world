package compose.world.composables.dynamic_graph.dialog_contents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
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
fun AddNodeDialog(
    onAdd: (title: String) -> Unit
) {
    var nodeTitle by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nodeTitle,
            label = {
                Text("Node title")
            },
            onValueChange = {
                nodeTitle = it
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ElevatedButton(onClick = {
            onAdd(nodeTitle)
        }) {
            Text("Add node")
        }
    }
}

@Preview
@Composable
private fun AddNodeDialogPrev() {
    AddNodeDialog(
        onAdd = {}
    )
}