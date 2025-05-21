package compose.world.composables.number_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPicker(
    values: List<String>,
    textStyle: TextStyle,
    visibleItemCount: Int = 2
) {
    Column {
        Text("Select timer duration")
        val textMeasurer = rememberTextMeasurer()

        val textSize = remember {
            val textLayoutResult = textMeasurer.measure(
                text = "1",
                style = textStyle
            )
            textLayoutResult.size
        }

        var dragAmount by remember { mutableFloatStateOf(0F) }
        var selectedValueIndex by remember { mutableStateOf(values.size / 2) }

        val animatedDragAmount by animateFloatAsState(targetValue = dragAmount)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .pointerInput(Unit) {
                    detectDragGestures (
                        onDrag = { _, drag ->
                            val newDragAmount = dragAmount + drag.y

                            val lastItemOffset = textSize.height * (values.size - 1)
                            val exactCenter = (size.height - textSize.height) / 2

                            if (newDragAmount <= exactCenter - lastItemOffset) {
                                return@detectDragGestures
                            }
                            if (newDragAmount >= exactCenter) {
                                return@detectDragGestures
                            }

                            dragAmount = newDragAmount
                        },
                        onDragEnd = {
                            val exactCenter = (size.height - textSize.height) / 2F
                            dragAmount = exactCenter - (selectedValueIndex * textSize.height)
                        }
                    )
                }
                .drawWithCache {
                    val centerRange = ((size.center.y - textSize.height) .. (size.center.y))
                    val exactHorizontalCenter = (size.width - textSize.width) / 2

                    onDrawBehind {
                        values.forEachIndexed { index, value ->
                            val numberYCoordinate = textSize.height * index + dragAmount
                            val isCentralItem = numberYCoordinate in centerRange
                            if (isCentralItem) { selectedValueIndex = index }

                            val deviationFromCenter =
                                if (isCentralItem) 0F
                                else if (numberYCoordinate < centerRange.start) centerRange.start - numberYCoordinate
                                else numberYCoordinate - centerRange.endInclusive

                            val horizontalAngle = 30F
                            val deviationSpread = ((deviationFromCenter) / textSize.height)
                            val numberXOffset = deviationSpread * -horizontalAngle

                            val layoutResult = textMeasurer.measure(
                                text = value,
                                style = textStyle.copy(
                                    color = if (isCentralItem) Color.Red else
                                        Color.Black.copy(1F - (deviationSpread / visibleItemCount))
                                )
                            )

                            translate(top = animatedDragAmount, left = exactHorizontalCenter) {
                                drawText(
                                    textLayoutResult = layoutResult,
                                    topLeft = Offset(
                                        x = numberXOffset,
                                        y = (textSize.height * index).toFloat()
                                    )
                                )
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(density.run { textSize.height.toDp() })
                .border(width = 1.dp, color = Color.Black)
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun IosPickerPrev() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        NumberPicker(
            values = listOf("5","10","15","20","25","30","35","40","45","50","55","60"),
            textStyle = TextStyle(fontSize = 32.sp),
            visibleItemCount = 4
        )
    }
}