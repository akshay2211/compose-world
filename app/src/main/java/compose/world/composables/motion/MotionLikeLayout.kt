package compose.world.composables.motion

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

enum class MotionLikeLayoutState(val progress: Float) {
    FULLY_EXPANDED(1F), HIDDEN(-1F), MINIMIZED(0F)
}

@Composable
fun MotionLikeLayout(
    state: MotionLikeLayoutState,
    animationSpec: AnimationSpec<Float> = spring(),
    initialContent: @Composable () -> Unit,
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = animationSpec
    )

    if (animatedProgress == MotionLikeLayoutState.HIDDEN.progress) return

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = abs(size.height * animatedProgress)
                }
        ) {
            initialContent()
        }

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = -size.height * (1F - animatedProgress)
                    }
            ) {
                topContent()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = size.height * (1F - animatedProgress)
                    }
            ) {
                bottomContent()
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun MotionLikeLayoutPrev() {
    var state by remember { mutableStateOf(MotionLikeLayoutState.FULLY_EXPANDED) }
//    var height by remember { mutableStateOf(200.dp) }

    MotionLikeLayout(
        state = state,
        initialContent = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Blue)
                    .padding(24.dp)
                    .border(width = 1.dp, color = Color.White)
                    .padding(4.dp)
                    .clickable {
                        state = MotionLikeLayoutState.FULLY_EXPANDED
                    },
                text = "Click to expand!",
                color = Color.White,
                fontSize = 24.sp
            )
        },
        topContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Black)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Red)
                        .padding(horizontal = 24.dp)
                        .padding(vertical = 64.dp)
                        .clickable (interactionSource = null, indication = null) {
                            state = MotionLikeLayoutState.HIDDEN
                        },
                    text = "Top content",
                    color = Color.White
                )
            }
        }, bottomContent = {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Yellow)
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 12.dp)
                    .clickable (interactionSource = null, indication = null) {
                        state = MotionLikeLayoutState.MINIMIZED
                    },
                text = "Bottom content",
                color = Color.Black
            )
        })
}