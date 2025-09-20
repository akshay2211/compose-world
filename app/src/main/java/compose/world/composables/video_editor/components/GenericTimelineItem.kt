package compose.world.composables.video_editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import compose.world.composables.video_editor.ShapeSmall
import compose.world.composables.video_editor.WhiteLight
import kotlin.math.roundToInt

@Composable
fun GenericTimelineItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    offset: Long,
    onClick: () -> Unit,
    onDragItem: (Long) -> Unit,
    onDragRight: (Long) -> Unit,
    onDragLeft: (Long) -> Unit,
    onChangeVerticalOrderBy: (Int) -> Unit,
    onDragEnd: () -> Unit,
    content: @Composable () -> Unit,
) {
    var verticalDrag by remember { mutableFloatStateOf(0F) }

    val dragSensitivity = 10.0
    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .widthIn(min = 16.dp)
            .clickable(onClick = onClick)
            .background(color = Color.LightGray, shape = ShapeSmall)
            .drawWithContent {
                drawContent()
                if (isSelected) {
                    drawRect(
                        color = Color.White,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                if (offset != 0L) {
                    val radius = 3.dp.toPx()
                    translate(left = -radius * 2, top = -radius * 2) {
                        drawCircle(
                            color = Color.Red,
                            radius = radius,
                            center = Offset(
                                x = size.width,
                                y = size.height
                            )
                        )
                    }
                }

            }
            .then(if (isSelected) Modifier.pointerInput(Unit) {
                detectDragGesturesAfterLongPress (
                    onDragStart = { verticalDrag = 0F },
                    onDragEnd = onDragEnd
                ) { _, dragAmount ->
                    onDragItem((dragAmount.x * dragSensitivity).toLong())
                    verticalDrag += dragAmount.y

                    if ((verticalDrag.roundToInt() / 100) == 0) return@detectDragGesturesAfterLongPress

                    if (verticalDrag > 0) onChangeVerticalOrderBy(1)
                    else onChangeVerticalOrderBy(-1)
                    verticalDrag = 0F
                }
            } else Modifier)
    ) {
        content()
        if (isSelected) {
            SelectionDragHandle(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .graphicsLayer {
                        translationX = -size.width
                    }.then(Modifier.pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            onDragLeft((dragAmount.x * dragSensitivity).toLong())
                        }
                    })
            )
            SelectionDragHandle(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .graphicsLayer {
                        translationX = size.width
                    }.then(Modifier.pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            onDragRight((dragAmount.x * dragSensitivity).toLong())
                        }
                    } )
            )
        }
    }
}

@Composable
fun SelectionDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(16.dp)
            .fillMaxHeight()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(8.dp)
                .background(color = WhiteLight)

        )
    }
}