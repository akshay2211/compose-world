package compose.world.composables.nav3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.Serializable

@Composable
fun Nav3Example() {

}

@Preview
@Composable
private fun Nav3Prev() {
    Nav3Example()
}


@Serializable
data class ScreenOne (
    val name: String,
    val age: Int
)

fun main() {

}