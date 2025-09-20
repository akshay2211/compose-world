package compose.world.composables.video_editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Int.toDp() : Dp {
    return LocalDensity.current.run { this@toDp.toDp() }
}

@Composable
fun Dp.toPx() : Float {
    return LocalDensity.current.run { this@toPx.toPx() }
}

@Composable
fun getWidthForDuration(durationInMillis: Long) : Dp {
    return (WIDTH_PER_MILLISECOND_DP * durationInMillis).dp
}

@Composable
fun getScreenWidth() : Dp {
    return LocalConfiguration.current.screenWidthDp.dp
}