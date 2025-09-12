package compose.world.composables.customizable_chart

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class ChartPoint(
    val value: Float,
    val delta: Float,
    val yCoordinate: Float
)

@Composable
fun CustomizableChart(
    values: List<ChartPoint>,
    selectedTimePointIndex: Int,
    verticalScale: Float = 0.25F,
    horizontalScale: Int = 6,
    heightScale: Float = 0.5F,
    overallScale: Float = 1F,
    clipToTop: Boolean = false,
    isHorizontallyAnimated: Boolean = true,
    onPointSelectionChanged: (Int) -> Unit
) {
    var chartProgress by remember { mutableFloatStateOf(0F) }
    var chartProgressIndex by remember { mutableIntStateOf(0) }

    var selectedPointIndex by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val screenWidth = getScreenWidth() / overallScale
    val spacing = density.run { 5.dp.toPx() * (1 + horizontalScale) }

    var dragX by remember { mutableFloatStateOf(0F) }
    val leftTranslation = remember(selectedPointIndex, spacing, screenWidth) {
        -maxOf(
            0F,
            (selectedPointIndex * spacing) - (screenWidth / 2)
        )
    }
    val animatedLeftTranslation by animateFloatAsState(leftTranslation, tween(50))

    // Sync drag to external changes
    LaunchedEffect(selectedTimePointIndex) { dragX = selectedTimePointIndex * spacing }
    LaunchedEffect(horizontalScale) { dragX = selectedPointIndex * spacing }

    // Adjust selected point index as drag changes
    LaunchedEffect(dragX) {
        val targetIndex = (dragX / spacing).toInt()
        val validatedIndex = targetIndex.coerceIn(0, values.lastIndex)
        selectedPointIndex = validatedIndex
        onPointSelectionChanged(validatedIndex)
        println("drag: $dragX")
        println("point changed to: $validatedIndex")

        if (validatedIndex != targetIndex) {
            dragX = validatedIndex * spacing
            println("dragX rolled back to $dragX")
        }
    }

    LaunchedEffect(Unit) {
        while (chartProgressIndex < values.lastIndex - 1) {
            animate(
                initialValue = 0F,
                targetValue = 1F,
                animationSpec = tween(durationMillis = 200, easing = LinearEasing)
            ) { value, _ ->
                chartProgress = value
            }
            chartProgressIndex++
        }
    }


    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .pointerInput(selectedPointIndex, horizontalScale) {
                detectTapGestures {
                    dragX = -leftTranslation + it.x / overallScale
                }
            }
            .pointerInput(horizontalScale) {
                detectDragGestures { change, dragAmount ->
                    val newDrag = (dragX - dragAmount.x * (horizontalScale / 5F) / overallScale).coerceAtLeast(0F)
                    dragX = newDrag
                }
            }
    ) {
            clipRect (top = if (clipToTop) 0F else -Float.MAX_VALUE, right = Float.MAX_VALUE) {
                scale(overallScale, Offset(x = 0F, y = size.height)) {

                    translate(top = size.height * heightScale, left = if (isHorizontallyAnimated) animatedLeftTranslation else leftTranslation) {
                        val linePath = Path().apply {
                            for (i in values.indices) {
                                val thisPoint = values[i]
                                val targetOffset = Offset(spacing * i, thisPoint.yCoordinate)
                                lineTo(targetOffset.x, targetOffset.y * verticalScale)
                            }
                        }
                        val bgPath = linePath.copy().apply {
                            // Ensure the path goes down till the bottom of the area (canvas)
                            val bottomY = size.height
                            // Here, Float.MaxValue messes it up for some reason, limited to x100 width.
                            lineTo(size.width * 100F, bottomY)
                            lineTo(0F, bottomY)
                        }


                        clipRect(
                            left = 0F,
                            right = (spacing * chartProgress) + chartProgressIndex * spacing,
                            top = -Float.MAX_VALUE,
                            bottom = size.height * 2
                        ) {
                            // Draw actual line
                            drawPath(linePath, color = Color.White, style = Stroke(width = 2.dp.toPx()))
                            // Draw white bg with alpha channel
                            drawPath(
                                path = bgPath, style = Fill, brush = Brush.verticalGradient(
                                    colors = listOf(Color.White.copy(0.1F), Color.White.copy(0.4F))
                                )
                            )
                        }

                        val leadingDotOffset = Offset(
                            x = (spacing * chartProgress) + chartProgressIndex * spacing,
                            y = ((values[chartProgressIndex].yCoordinate + values[chartProgressIndex + 1].delta * chartProgress) * verticalScale)
                        )
                        val selectedValue = values[selectedPointIndex]
                        val selectedDotOffset = Offset(
                            x = selectedPointIndex * spacing,
                            y = (selectedValue.yCoordinate * verticalScale)
                        )
                        // Draw leading dot and selected dot
                        drawPoints(
                            points = listOf(leadingDotOffset, selectedDotOffset),
                            pointMode = PointMode.Points,
                            strokeWidth = 6.dp.toPx(),
                            color = Color.White,
                            cap = StrokeCap.Round
                        )

                        translate(
                            left = selectedDotOffset.x,
                            top = selectedDotOffset.y
                        ) {
                            // Draw vertical selection line
                            drawLine(
                                brush = Brush.verticalGradient(listOf(Color.White, Color.Transparent)),
                                start = Offset.Zero, end = Offset(x = 0F, y = size.height)
                            )
                            // Draw description text
                            translate(left = 8.dp.toPx(), top = 16.dp.toPx()) {
                                val isIncreased = selectedValue.delta < 0
                                val prefix = if (isIncreased) '+' else ' '
                                val color = if (isIncreased) Color(0xFFAAFFAA) else Color(0xFFFFAAAA)
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = "$prefix${-selectedValue.delta} \$",
                                    style = TextStyle(color = color, fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }
                }
            }
    }
}

@Preview
@Composable
private fun ChartUsage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFb486c6),
                        Color(0xFF3c31a3),
                        Color(0xFF070655),
                        Color(0xFF070655),
                    ),
                    start = Offset(0F, 0F),
                    end = Offset(2000F, 2500F)
                )
            )
            .padding(top = 200.dp)
    ) {
        var selectedPointIndex by remember { mutableIntStateOf(0) }
        var selectedTimeIndex by remember { mutableIntStateOf(0) }
        var selectedTimeIndexByUser by remember { mutableIntStateOf(0) }

        val selectedValue by animateFloatAsState(targetValue = values[selectedPointIndex], animationSpec = tween(200))
        LaunchedEffect(selectedPointIndex) {
            selectedTimeIndex = (selectedPointIndex / 3).coerceIn(0, timestamps.lastIndex)
        }
        Box {
            var horizontalScaleProgress by rememberSaveable { mutableFloatStateOf(0.5F) }
            var graphAnimationStopped by rememberSaveable { mutableStateOf(false) }
            var verticalScaleProgress by rememberSaveable { mutableFloatStateOf(0.25F) }
            var heightScaleProgress by rememberSaveable { mutableFloatStateOf(0.5F) }
            var overallScaleProgress by rememberSaveable { mutableFloatStateOf(1F) }
            CustomizableChart(
                values = values.mapToChartPoints(),
                selectedTimePointIndex = (selectedTimeIndexByUser * 3),
                verticalScale = verticalScaleProgress.coerceAtLeast(0.01F),
                horizontalScale = ((horizontalScaleProgress * 100).toInt() / 5).coerceAtLeast(1),
                heightScale = heightScaleProgress,
                overallScale = overallScaleProgress,
                isHorizontallyAnimated = !graphAnimationStopped,
                onPointSelectionChanged = {
                    selectedPointIndex = it
                }
            )
            ChartSlider(
                modifier = Modifier
                    .fillMaxWidth(0.25F)
                    .offset(y = -6.dp)
                    .align(Alignment.BottomCenter),
                initialProgress = 0.15F,
                onProgressChanged = {
                    horizontalScaleProgress = it * 3F
                },
                onDragStart = {
                    graphAnimationStopped = true
                },
                onDragEnd = {
                    graphAnimationStopped = false
                }
            )

            ChartSlider(
                modifier = Modifier
                    .fillMaxWidth(0.25F)
                    .offset(y = -32.dp)
                    .align(Alignment.BottomCenter),
                initialProgress = 0.5F,
                onProgressChanged = {
                    overallScaleProgress = it * 2F
                }, onDragStart = {
                    graphAnimationStopped = true
                }, onDragEnd = {
                    graphAnimationStopped = false
                }
            )

            ChartSlider(
                modifier = Modifier
                    .fillMaxWidth(0.25F)
                    .offset(x = 32.dp)
                    .graphicsLayer {
                        translationY = -size.width / 2
                        translationX = -size.width / 2F
                    }
                    .rotate(90F)
                    .align(Alignment.BottomStart),
                initialProgress = 0.9F,
                onProgressChanged = {
                    verticalScaleProgress = (1 - it) * 3F
                }
            )

            ChartSlider(
                modifier = Modifier
                    .fillMaxWidth(0.25F)
                    .offset(x = 16.dp)
                    .graphicsLayer {
                        translationY = -size.width / 2
                    }
                    .rotate(90F)
                    .align(Alignment.BottomEnd),
                initialProgress = 0.85F,
                onProgressChanged = {
                    heightScaleProgress = (1 - it) * 3F
                }
            )

            Row (verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = "\$",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(start = 3.dp),
                    text = "$selectedValue",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val listState = rememberLazyListState()
        val screenWidth = getScreenWidth()
        LaunchedEffect(selectedTimeIndex) {
            listState.animateScrollToItem(selectedTimeIndex, (-screenWidth / 2 + 100).roundToInt())
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            state = listState
        ) {
            itemsIndexed(timestamps) { index, it ->
                val isSelected = selectedTimeIndex == index
                TextButton(onClick = {
                    selectedTimeIndexByUser = index
                }) {
                    Text(
                        modifier = Modifier.drawBehind {
                            if (!isSelected) return@drawBehind
                            scale(2F) {
                                drawRoundRect(
                                    color = Color.White,
                                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                                )
                            }
                        },
                        text = it, color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}