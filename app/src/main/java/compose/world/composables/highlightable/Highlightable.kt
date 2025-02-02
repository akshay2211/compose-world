package compose.world.composables.highlightable

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

data class HighlightOptions (
    val baseColor: Color,
    val baseScale: Float,
    val rippleColor: Color,
    val rippleScale: Float,
    val transparencyPath: (center: Offset, size: Size) -> Path
)

val DefaultHighlightOptions = HighlightOptions(
    baseColor = Color.Blue.copy(0.7F),
    baseScale = 10F,
    rippleColor = Color.White.copy(0.7F),
    rippleScale = 2F,
    transparencyPath = { center, size->
        Path().apply {
            addOval(
                oval = Rect(
                    center = center,
                    radius = size.width / 2
                )
            )
        }
    }
)

fun Modifier.highlight(
    enabled: Boolean,
    options: HighlightOptions = DefaultHighlightOptions
) = composed {
    if (enabled) {
        val infiniteAnimation by rememberInfiniteTransition(label = "infinite_highlight")
            .animateFloat(
                initialValue = 0F,
                targetValue = 1F,
                animationSpec = infiniteRepeatable(tween(durationMillis = 2000), repeatMode = RepeatMode.Restart),
                label = "infinite_restart_animation"
            )
        var isVisible by remember { mutableStateOf(false) }
        val animatedScale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0F,
            animationSpec = tween(1000)
        )
        LaunchedEffect(Unit) { isVisible = true }

        Modifier
            .zIndex(10F)
            .drawBehind {
                clipPath(
                    path = options.transparencyPath.invoke(center, size),
                    clipOp = ClipOp.Difference
                ) {
                    drawCircle(
                        color = options.baseColor,
                        radius = size.height * options.baseScale * (animatedScale)
                    )

                    drawCircle(
                        color = options.rippleColor.copy((1F - infiniteAnimation)),
                        radius = size.width / 2 * options.rippleScale * infiniteAnimation
                    )
                }
            }
    } else {
        Modifier
    }
}


@Composable
fun ContentWithPopUp(
    mainContent : @Composable () -> Unit,
    popUpColor: Color,
    popUpContent: @Composable () -> Unit,
    isPopUpVisible: Boolean
) {
    Column {
        mainContent()

        if (isPopUpVisible) {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(surface = popUpColor)
            ) {
                DropdownMenu(
                    modifier = Modifier
                        .padding(6.dp)
                        .width(200.dp),
                    expanded = true,
                    onDismissRequest = {}
                ) {
                    popUpContent()
                }
            }
        }
    }
}

/** Convention method for using highlight modifier with a popup */
@Composable
fun ContentWithHighlightPopUp(
    mainContent : @Composable () -> Unit,
    popUpColor: Color,
    description: String,
    isPopUpVisible: Boolean,
    highlightOptions: HighlightOptions = DefaultHighlightOptions
        .copy(baseColor = popUpColor.copy(0.5F)),
    onNext: () -> Unit
) {
    ContentWithPopUp(
        mainContent = {
            Box(
                modifier = Modifier.highlight(
                    enabled = isPopUpVisible,
                    options = highlightOptions
                )
            ) {
                mainContent()
            }
        },
        popUpColor = popUpColor,
        popUpContent = {
            Column {
                Text(
                    text = description,
                    color = Color.White
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .border(
                            width = 1.dp,
                            color = Color.White
                        )
                        .clickable(onClick = onNext)
                        .padding(6.dp),
                    text = "Next >",
                    color = Color.White
                )
            }
        },
        isPopUpVisible = isPopUpVisible
    )
}