package compose.world.composables.customizable_chart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.nio.file.Files.size

@Composable
fun ChartSlider(
    modifier: Modifier = Modifier,
    thickness: Dp = 4.dp,
    focusedScale: Float = 2F,
    trackColor: Color = Color.White,
    thumbColor: Color = Color.White,
    initialProgress: Float = 0.5F,
    onProgressChanged: (Float) -> Unit = {},
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {}
) {
    var progress by rememberSaveable { mutableFloatStateOf(initialProgress) }
    var dragX by rememberSaveable { mutableFloatStateOf(0F) }
    var isDragged by rememberSaveable { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isDragged) focusedScale else 1F)
    val trackHeight = thickness * scale

    LaunchedEffect(progress) {
        onProgressChanged(progress)
    }
    Canvas(
        modifier = modifier
            .height((thickness + 2.dp) * 3)
            .border(width = 1.dp, color = Color.White.copy(0.1F))
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    onDragStart()
                    dragX = it.x
                    isDragged = true
                }, onDragEnd = {
                    onDragEnd()
                    isDragged = false
                }) { _, dragAmount ->
                    dragX += dragAmount.x
                    progress = (dragX / size.width).coerceIn(0F, 1F)
                }
            }
    ) {
        val radius = thickness.toPx() * scale
        translate(top = (size.height - trackHeight.toPx()) / 2) {
            drawRoundRect(
                color = trackColor,
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                size = Size(size.width, height = trackHeight.toPx())
            )
        }
        drawCircle(
            color = thumbColor,
            center = Offset(x = size.width * progress, y = size.center.y),
            radius = radius
        )
    }
}