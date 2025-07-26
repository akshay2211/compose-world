package compose.world.composables.compose_ninja

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class CutInfo(
    val cutPoint: Pair<Offset, Offset>,
    originalSize: Size,
    val isLeftSlice: Boolean,
) {
    val leftTop = when (isLeftSlice) {
        true -> Offset.Zero
        false -> cutPoint.second
    }
    val leftBottom = when (isLeftSlice) {
        true -> Offset(x = 0F, y = originalSize.height)
        false -> cutPoint.first
    }

    val rightTop = when (isLeftSlice) {
        true -> cutPoint.first
        false -> Offset(x = originalSize.width, y = originalSize.height)
    }
    val rightBottom = when (isLeftSlice) {
        true -> cutPoint.second
        false -> Offset(x = originalSize.width, y = 0F)
    }
}