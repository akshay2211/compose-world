package compose.world.composables.compose_ninja

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeNinja() {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFCD7537)
                ).padding(top = 18.dp, bottom = 6.dp).systemBarsPadding(),
            text = "COMPOSE Ninja",
            fontSize = 26.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(thickness = 5.dp)


        FruitNinjaLayout (
            modifier = Modifier.weight(1F)
        ) {
            addFruit {
                Fruit(
                    label = "Material3Button",
                    offset = Offset(100F,50F)
                ) {
                    Button(onClick = {}) {
                        Text("THIS IS A BUTTON!")
                    }
                }
            }

            addFruit {
                Fruit(
                    label = "Material3Button2",
                    offset = Offset(100F,200F)
                ) {
                    var isChecked by remember { mutableStateOf(false) }
                    Switch(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                        }
                    )
                }
            }

            addFruit {
                Fruit(
                    label = "FloatingActionButton",
                    offset = Offset(100F,400F)
                ) {
                    FloatingActionButton(
                        onClick = {}
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                    }
                }
            }

            addFruit {
                Fruit(
                    label = "Checkbox",
                    offset = Offset(400F,420F)
                ) {
                    var isChecked by remember { mutableStateOf(false) }

                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                        }
                    )
                }
            }

            addFruit {
                Fruit(
                    label = "BackgroundedText",
                    offset = Offset(400F,200F)
                ) {
                    var isChecked by remember { mutableStateOf(false) }

                    Text(
                        modifier = Modifier
                            .background(
                                color = Color(0xFF8080BB)
                            ).padding(6.dp),
                        text = "This is a text!",
                        fontSize = 26.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun FruitNinjaLayout(
    modifier: Modifier = Modifier,
    content: FruitNinjaLayoutScope.() -> Unit,
) {
    val fruits = remember { mutableStateMapOf<String, Fruit>() }
    // Add fruits
    LaunchedEffect(Unit) {
        val scope = object : FruitNinjaLayoutScope {
            override fun addFruit(block: () -> Fruit) {
                val fruit = block()
                val uniqueKey = fruit.label
                if (uniqueKey in fruits.keys) return

                fruits[uniqueKey] = fruit
            }
        }
        content(scope)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .trail(
                onFinish = { trailLines ->
                    findCutPoints(trailLines, fruits.values.toList()).forEach { (label, localCutPoint) ->
                        val fruit = fruits[label]!!

                        val leftSlice = fruit.copy(
                            label = fruit.label + "left-$localCutPoint",
                            cutInfos = fruit.cutInfos + CutInfo(
                                cutPoint = localCutPoint,
                                originalSize = fruit.size,
                                isLeftSlice = true
                            ),
                            isLeftSlice = true
                        )

                        val rightSlice = fruit.copy(
                            label = fruit.label + "right-$localCutPoint",
                            cutInfos = fruit.cutInfos + CutInfo(
                                cutPoint = localCutPoint,
                                originalSize = fruit.size,
                                isLeftSlice = false
                            ),
                            isLeftSlice = false
                        )
                        println("Added: $leftSlice")
                        // Add sliced and remove original
                        fruits.remove(fruit.label)
                        fruits[leftSlice.label] = leftSlice
                        fruits[rightSlice.label] = rightSlice
                    }
                }
            )
    ) {
        fruits.values.forEach { fruit ->
            Box(
                modifier = Modifier
                    .onSizeChanged { fruit.size = it.toSize() }
                    .graphicsLayer {
                        translationX = fruit.translationX
                        translationY = fruit.translationY
                    }
                    .drawWithContent {
                        val cutInfos = fruit.cutInfos
                        if (cutInfos.isEmpty()) {
                            drawContent()
                            return@drawWithContent
                        }

                        cutInfos.foldRight(
                            initial = { this@drawWithContent.drawContent() }
                        ) { cutInfo, content ->
                            {
                                clipPath(
                                    path = Path().apply {
                                        moveTo(cutInfo.leftTop.x, cutInfo.leftTop.y)
                                        lineTo(cutInfo.rightTop.x, cutInfo.rightTop.y)
                                        lineTo(cutInfo.rightBottom.x, cutInfo.rightBottom.y)
                                        lineTo(cutInfo.leftBottom.x, cutInfo.leftBottom.y)
                                        close()
                                    }
                                ) {
                                    content()
                                }
                            }
                        }.invoke()
                    }
                    .pointerInput(fruit.label) {
                        detectDragGestures { _, dragAmount ->
                            fruit.translationX += dragAmount.x
                            fruit.translationY += dragAmount.y
                        }
                    }
            ) {
                fruit.content()
            }
        }
    }
}

fun Modifier.trail(onFinish: (lines: List<Pair<Offset, Offset>>) -> Unit) = composed {
    var isTrailInProgress by remember { mutableStateOf(false) }
    var startOffset = remember { Offset.Zero }
    val cutTrail = remember { mutableStateListOf<Pair<Offset, Offset>>() }

    LaunchedEffect(isTrailInProgress) {
        if (!isTrailInProgress) {
            onFinish(cutTrail)
            cutTrail.clear()
        } else {
            while (true) {
                delay(50)
                cutTrail.removeFirstOrNull()
            }
        }
    }

    pointerInput(Unit) {
        detectDragGestures(
            onDragStart = {
                startOffset = it
                isTrailInProgress = true
            },
            onDragEnd = {
                isTrailInProgress = false
            }
        ) { _, dragAmount ->
            if (cutTrail.isEmpty()) {
                cutTrail.add(startOffset to startOffset + dragAmount)
            } else {
                val lastPoint = cutTrail.last().second
                cutTrail.add(lastPoint to lastPoint + dragAmount)
            }
        }
    }
        .drawBehind {
            cutTrail.forEachIndexed { index, it ->
                val indexProgress = (index + 1) / cutTrail.size.toFloat()

                drawLine(
                    color = Color.Black,
                    start = it.first,
                    end = it.second,
                    strokeWidth = (10 * indexProgress).dp.toPx()
                )
            }
        }
}

interface FruitNinjaLayoutScope {
    fun addFruit(block: () -> Fruit)
}

@Preview(showBackground = true)
@Composable
private fun ComposeNinjaPrev() {
    ComposeNinja()
}