package compose.world.composables.draggable_column

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

/**
* SubcomposeLayout that [SubcomposeMeasureScope.subcompose]s [content]
* and gets total size of [content] and passes this size to [dependentContent].
* This layout passes exact size of content unlike
* BoxWithConstraints which returns [Constraints] that doesn't match Composable dimensions under
* some circumstances
*
* @param placeMainContent when set to true places main content. Set this flag to false
* when dimensions of content is required for inside [content]. Just measure it then pass
* its dimensions to any child composable
*
* @param content Composable is used for calculating size and pass it
* to Composables that depend on it
*
* @param dependentContent Composable requires dimensions of [content] to set its size.
* One example for this is overlay over Composable that should match [content] size.
*
*/

@Composable
fun DimensionSubcomposeLayout(
    modifier: Modifier = Modifier,
    placeMainContent: Boolean = true,
    mainContent: @Composable () -> Unit,
    dependentContent: @Composable (Size) -> Unit
) {
    SubcomposeLayout(
        modifier = modifier
    ) { constraints: Constraints ->

        // Subcompose(compose only a section) main content and get Placeable
        val mainPlaceables: List<Placeable> = subcompose(SlotsEnum.Main, mainContent)
            .map {
                it.measure(constraints.copy(minWidth = 0, minHeight = 0))
            }

        // Get max width and height of main component
        var maxWidth = 0
        var maxHeight = 0

        mainPlaceables.forEach { placeable: Placeable ->
            maxWidth += placeable.width
            maxHeight = placeable.height
        }

        val dependentPlaceables: List<Placeable> = subcompose(SlotsEnum.Dependent) {
            dependentContent(Size(maxWidth.toFloat(), maxHeight.toFloat()))
        }
            .map { measurable: Measurable ->
                measurable.measure(constraints)
            }


        layout(maxWidth, maxHeight) {

            if (placeMainContent) {
                mainPlaceables.forEach { placeable: Placeable ->
                    placeable.placeRelative(0, 0)
                }
            }

            dependentPlaceables.forEach { placeable: Placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}

enum class SlotsEnum { Main, Dependent }


@Preview(showBackground = true)
@Composable
private fun DimensionSubcomposeLayoutPrev() {
    val content = @Composable {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Red)
        )
    }

    val density = LocalDensity.current

    DimensionSubcomposeLayout(
        mainContent = { content() },
        dependentContent = { size: Size ->
            content()
            val dpSize = density.run {size.toDpSize() }
            Box(Modifier.size(dpSize).border(3.dp, Color.Green))
        },
        placeMainContent = false
    )
}