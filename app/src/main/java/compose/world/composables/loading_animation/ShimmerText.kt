package compose.world.composables.loading_animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ShimmerText(
    text: String,
    style: TextStyle,
    shimmerSecondaryColor: Color = Color.Black,
    shimmerPrimaryColor: Color = Color.White
) {
    Box(
        modifier = Modifier
            .background(color = shimmerSecondaryColor),
        contentAlignment = Alignment.Center
    ) {
        val shimmerProgress by rememberInfiniteTransition(label = "shimmer")
            .animateFloat(
                initialValue = 0F,
                targetValue = 1F,
                animationSpec = infiniteRepeatable(
                    tween(
                        durationMillis = 2000,
                        easing = LinearEasing
                    )
                ), label = "progress"
            )


        val textMeasurer = rememberTextMeasurer()
        val textWidth = remember (text) { textMeasurer.measure(text = text, style = style).size.width }

        val offset = 2 * textWidth * shimmerProgress
        Text(
            modifier = Modifier,
            text = text,
            color = Color.Black,
            style = style.copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        shimmerSecondaryColor,
                        shimmerPrimaryColor,
                        shimmerPrimaryColor,
                        shimmerSecondaryColor
                    ),
                    startX = -textWidth + offset,
                    endX = offset
                )
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ShimmerTextPrev() {
    ShimmerText(
        text = "Searching the web...",
        style = TextStyle(
            color = Color.Black
        ),
        shimmerPrimaryColor = Color.White,
        shimmerSecondaryColor = Color.Black
    )
}