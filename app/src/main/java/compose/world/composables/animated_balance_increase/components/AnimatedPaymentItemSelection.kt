package compose.world.composables.animated_balance_increase.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedPaymentItemSelection(
    cards: List<String>,
    selectedCardIndex: Int,
    spacing: Dp = 6.dp,
    onCardSelected: (Int) -> Unit,
) {
    val density = LocalDensity.current
    var itemHeight by remember { mutableFloatStateOf(0F) }

    val animatedSelectionStrokeOffset by animateFloatAsState(
        targetValue = (density.run { spacing.toPx() } + itemHeight) * selectedCardIndex,
        animationSpec = tween(1000),
        label = "animatedSelectionStrokeOffset"
    )

    Column(
        modifier = Modifier.drawWithCache {
            val totalSpacing = (spacing.toPx()) * (cards.size - 1)
            itemHeight = (size.height - totalSpacing) / cards.size

            onDrawWithContent {
                drawContent()

                translate(top = animatedSelectionStrokeOffset) {
                    drawRoundRect(
                        color = Color.Black,
                        size = Size(width = size.width, height = itemHeight),
                        cornerRadius = CornerRadius(x = 8.dp.toPx(), y = 8.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
    ) {
        cards.forEachIndexed { index, cardNumber ->
            PaymentItem(
                cardNumber = cardNumber,
                selected = index == selectedCardIndex,
                onClick = { onCardSelected(index) }
            )
            if (index != cards.lastIndex) {
                Spacer(modifier = Modifier.height(spacing))
            }
        }
    }
}