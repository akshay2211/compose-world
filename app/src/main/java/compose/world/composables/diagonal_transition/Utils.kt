package compose.world.composables.diagonal_transition

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

fun slideInDiagonally() = slideIn(
    initialOffset = { IntOffset(it.width, it.height) },
    animationSpec = tween(durationMillis = 1000)
)

fun slideOutDiagonally() = slideOut(
    targetOffset = { IntOffset(it.width, it.height) },
    animationSpec = tween(durationMillis = 1000)
)