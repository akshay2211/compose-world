package compose.world.composables.diagonal_transition.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.ScreenButtonRight(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    isButtonVisible: Boolean,
    buttonAppearAnimationDelay: Int,
    shouldExpand: Boolean,
    onClick: () -> Unit
) {
    val buttonScale by animateFloatAsState(
        if (isButtonVisible) 1F else 0F,
        animationSpec = tween(durationMillis = 500, delayMillis = buttonAppearAnimationDelay)
    )

    val animatedExpansionValue by animateFloatAsState(
        targetValue = if (shouldExpand) 20F else 1F,
        animationSpec = tween(1000)
    )

    Box(
        modifier = modifier
            .size(150.dp)
            .align(
                Alignment.BottomEnd
            )
            .graphicsLayer {
                scaleX = buttonScale * animatedExpansionValue
                scaleY = buttonScale * animatedExpansionValue
                translationX = (1 - buttonScale) * size.width
                translationY = (1 - buttonScale) * size.height
            }
            .clip(
                RoundedCornerShape(
                    topStartPercent = 100
                )
            )
            .background(
                color = color,
                shape = RoundedCornerShape(
                    topStartPercent = 100
                )
            )
            .clickable(interactionSource = null, indication = null) {
                onClick()
            }
            .padding(32.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                },
            text = text,
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

@Composable
fun BoxScope.ScreenButtonLeft(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    isButtonVisible: Boolean,
    buttonAppearAnimationDelay: Int,
    shouldExpand: Boolean,
    onClick: () -> Unit
) {
    var buttonScale by remember { mutableFloatStateOf(0F) }
    LaunchedEffect(isButtonVisible) {
        if (!isButtonVisible) buttonScale = 0F
        else {
            animate(
                initialValue = 0F,
                targetValue = 1F,
                animationSpec = tween(durationMillis = 500, delayMillis = buttonAppearAnimationDelay)
            ) { value, _->
                buttonScale = value
            }
        }
    }


    val animatedExpansionValue by animateFloatAsState(
        targetValue = if (shouldExpand) 20F else 1F,
        animationSpec = tween(1000)
    )

    Box(
        modifier = modifier
            .size(150.dp)
            .align(
                Alignment.BottomStart
            )
            .graphicsLayer {
                scaleX = buttonScale * animatedExpansionValue
                scaleY = buttonScale * animatedExpansionValue
                translationX = (1 - buttonScale) * -size.width
                translationY = (1 - buttonScale) * size.height
            }
            .clip(
                RoundedCornerShape(
                    topEndPercent = 100
                )
            )
            .background(
                color = color,
                shape = RoundedCornerShape(
                    topEndPercent = 100
                )
            )
            .clickable(interactionSource = null, indication = null) {
                onClick()
            }
            .padding(32.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                },
            text = text,
            fontSize = 20.sp,
            color = Color.White
        )
    }
}