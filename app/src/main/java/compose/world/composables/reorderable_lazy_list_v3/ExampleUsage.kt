package compose.world.composables.reorderable_lazy_list_v3

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ExampleUsage(modifier: Modifier = Modifier) {
    val items = rememberReorderableLazyListItems(items = listOf(10,20,30,40))
    ReorderableLazyList(
        items = items
    ) { index, it->
        Text(
            text = it.toString()
        )
    }
}