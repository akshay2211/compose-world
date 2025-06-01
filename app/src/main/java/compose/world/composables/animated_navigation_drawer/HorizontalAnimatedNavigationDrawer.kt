package compose.world.composables.animated_navigation_drawer

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalAnimatedNavigationDrawer(
    animateToScale: Float = 0.6F
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    var isDrawerOpen by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0F) }
    val animatedProgress by animateFloatAsState(progress, label = "scree_scale")

    // When drawer is toggled, start animation
    LaunchedEffect(isDrawerOpen) {
        val animationTargetValue = if (isDrawerOpen) 1F else 0F

        animate(
            initialValue = progress,
            targetValue = animationTargetValue,
            animationSpec = tween(durationMillis = 1000)
        ) { value, _ -> progress = value }
    }

    // Container for both navigation items & main screen
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        InstagramDummyScreen(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures (
                        onDragEnd = {
                            progress = if (progress > 0.5F) 1F else 0F
                        }
                    ) { change, dragAmount ->
                        progress = (progress + (dragAmount / 200F)).coerceIn(0F, 1F)
                    }
                }
                .graphicsLayer {
                    val scaleDecreaseByProgress = animatedProgress * (1F - animateToScale)
                    scaleX = 1F - scaleDecreaseByProgress
                    scaleY = 1F - scaleDecreaseByProgress
                    rotationY = -10 * animatedProgress
                    translationX = screenWidth.toPx() * (scaleDecreaseByProgress / 2)
                }
                .clip(RoundedCornerShape(size = 32.dp * animatedProgress)),
            onDrawerClicked = { isDrawerOpen = !isDrawerOpen },
            onScreenClicked = { isDrawerOpen = false }
        )


        DummyHorizontalNavigationDrawer(
            screenHeight = screenHeight,
            animateToScale = animateToScale,
            progress = animatedProgress
        )
    }
}

@Preview (showBackground = true)
@Composable
private fun AnimatedNavigationDrawerPrev() {
    HorizontalAnimatedNavigationDrawer(
        animateToScale = 0.6F
    )
}