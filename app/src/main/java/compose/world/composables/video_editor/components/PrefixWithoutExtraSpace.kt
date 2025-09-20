package compose.world.composables.video_editor.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.world.composables.video_editor.WhiteLight

@Composable
fun PrefixWithoutExtraSpace(
    prefix: @Composable (Modifier) -> Unit,
    padding: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    Box {
        content()
        prefix(
            Modifier
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    translationX = -padding.toPx() - size.width
                })
    }
}

@Composable
fun PrefixIconWithoutExtraSpace(
    padding: Dp = 24.dp,
    icon: Int,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    PrefixWithoutExtraSpace(
        prefix = { modifier->
            Icon(
                modifier = modifier
                    .size(16.dp)
                    .clickable(onClick = onClick),
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                tint = WhiteLight
            )
        },
        padding = padding
    ) { content() }
}