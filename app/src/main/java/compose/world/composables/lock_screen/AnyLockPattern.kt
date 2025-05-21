package compose.world.composables.lock_screen

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Composable
fun AnyLockPattern(
    rowCount: Int = 4,
    columnCount: Int = 4,
    columnPadding: Dp = 32.dp,
    symbolSize: Dp,
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 0.dp,
    selectionErrorRadius: Dp = 12.dp,
    lineParams: LineParams = LineParams(),
    customDotContent: @Composable (isSelected: Boolean) -> Unit
) {
    val config = LocalConfiguration.current
    val screenWidthDp = config.screenWidthDp.dp - horizontalPadding * 2
    val remainingScreenWidthWithoutDots = screenWidthDp - symbolSize * rowCount
    val rowPadding = remainingScreenWidthWithoutDots / (rowCount - 1)

    val widgetHeight = symbolSize * columnCount + columnPadding * (columnCount - 1)
    val widgetWidth = symbolSize * rowCount + rowPadding * (rowCount - 1)

    var dragCoordinate by remember { mutableStateOf(Offset.Zero) }
    val selectedDotCoordinates = remember { mutableStateListOf<IntOffset>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .clip(RectangleShape) // Clip does not allow drawLine command to draw outside the viewport
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .height(widgetHeight)
            .width(widgetWidth)
            .drawBehind {
                val sectionHeight = (symbolSize + columnPadding).toPx()
                val sectionWidth = (symbolSize + rowPadding).toPx()

                selectedDotCoordinates.forEachIndexed { index, coordinate ->
                    val currentDotOffset = Offset(
                        x = coordinate.x * sectionWidth + symbolSize.toPx() / 2,
                        y = coordinate.y * sectionHeight + symbolSize.toPx() / 2
                    )

                    if (index == selectedDotCoordinates.lastIndex) {
                        // Draw line to our current drag coordinate
                        drawLine(
                            start = currentDotOffset,
                            end = dragCoordinate,
                            strokeWidth = lineParams.strokeWidth.toPx(),
                            cap = lineParams.cap,
                            pathEffect = lineParams.pathEffect,
                            color = lineParams.color,
                            alpha = lineParams.alpha,
                            colorFilter = lineParams.colorFilter,
                            blendMode = lineParams.blendMode
                        )
                    } else {
                        // Connect selected dot coordinates
                        val nextDot = selectedDotCoordinates[index + 1]
                        val nextDotOffset = Offset(
                            x = nextDot.x * sectionWidth + symbolSize.toPx() / 2,
                            y = nextDot.y * sectionHeight + symbolSize.toPx() / 2
                        )
                        drawLine(
                            start = currentDotOffset,
                            end = nextDotOffset,
                            strokeWidth = lineParams.strokeWidth.toPx(),
                            cap = lineParams.cap,
                            pathEffect = lineParams.pathEffect,
                            color = lineParams.color,
                            alpha = lineParams.alpha,
                            colorFilter = lineParams.colorFilter,
                            blendMode = lineParams.blendMode
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragStartCoordinate ->
                        dragCoordinate = dragStartCoordinate
                    },
                    onDragEnd = {
                        selectedDotCoordinates.clear()
                    },
                    onDrag = { _, dragAmount ->
                        dragCoordinate += dragAmount

                        val dotRowIndex = findDotHorizontalIndex(
                            dragCoordinate = dragCoordinate,
                            dotSize = symbolSize,
                            rowPadding = rowPadding,
                            dotSelectionErrorRadius = selectionErrorRadius
                        )
                        val dotColumnIndex = findDotVerticalIndex(
                            dragCoordinate = dragCoordinate,
                            dotSize = symbolSize,
                            columnPadding = columnPadding,
                            dotSelectionErrorRadius = selectionErrorRadius
                        )

                        if (dotRowIndex == null || dotColumnIndex == null) return@detectDragGestures
                        if (dotColumnIndex !in 0 until columnCount) return@detectDragGestures
                        if (dotRowIndex !in 0 until rowCount) return@detectDragGestures

                        val dotCoordinate = IntOffset(x = dotRowIndex, y = dotColumnIndex)
                        if (dotCoordinate in selectedDotCoordinates) return@detectDragGestures

                        selectedDotCoordinates.add(dotCoordinate)
                    }
                )
            }
    ) {
        repeat(columnCount) { columnIndex ->
            repeat(rowCount) { rowIndex ->
                val coordinate = IntOffset(x = rowIndex, y = columnIndex)
                val isSelected = coordinate in selectedDotCoordinates



                Box(
                    modifier = Modifier
                        .size(symbolSize)
                        .offset(
                            x = symbolSize * rowIndex,
                            y = symbolSize * columnIndex
                        ) // Natural offset for row/column behaviour
                        .offset(
                            x = rowPadding * rowIndex,
                            y = columnPadding * columnIndex
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    customDotContent(isSelected)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun LockPatternPrev() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnyLockPattern(
            rowCount = 3,
            columnCount = 3,
            symbolSize = 16.dp,
            horizontalPadding = 88.dp,
            columnPadding = 64.dp,
            verticalPadding = 24.dp,
            selectionErrorRadius = 16.dp,
            customDotContent = { isSelected ->
                Text(
                    text = if (isSelected) "X" else "O",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            lineParams = LineParams(
                strokeWidth = 2.dp
            )
        )
    }
}