package compose.world

import androidx.compose.runtime.Composable

data class ExampleItem(
    val title: String,
    val description: String,
    val content: @Composable () -> Unit
)