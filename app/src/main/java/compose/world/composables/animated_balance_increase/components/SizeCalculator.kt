package compose.world.composables.animated_balance_increase.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

@Composable
fun SizeCalculator(
    modifier: Modifier = Modifier,
    modifiersAfterSizeCalculation: @Composable (calculatedSize: IntSize) -> Modifier,
    onSizeChanged: (calculatedSize: IntSize) -> Unit,
    content: @Composable (calculatedSize: IntSize) -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box (
        modifier = modifier
            .then(
                if (size == IntSize.Zero) Modifier
                    .graphicsLayer {
                        alpha = 0F
                    }.onSizeChanged {
                        size = it
                        onSizeChanged(it)
                    }
                else modifiersAfterSizeCalculation(size)
            )
    ) {
        content(size)
    }
}