package compose.world.composables.practice_3d

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import compose.world.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


class Point3D(
    x: Float,
    y: Float,
    z: Float,
) {
    val x by mutableFloatStateOf(x)
    var y by mutableFloatStateOf(y)
    var z by mutableFloatStateOf(z)

    fun to2D(distance: Float, xOffset: Float, yOffset: Float): Point2D {
        val x2D = x * (distance / z) + xOffset
        val y2D = y * (distance / z) + yOffset
        return Point2D(x2D, y2D)
    }
}

class Point2D(
    val x: Float,
    val y: Float,
) {
    fun asOffset() = Offset(x, y)
}

val topCoordinates = listOf(
    Point3D(0F, -5F, -5F),
//    Point3D(-10F, -10F, 5F),

    Point3D(-10F, 0F, 5F),
    Point3D(10F, 0F, 5F)
)

val bottomCoordinates = listOf(
    Point3D(10F, 10F, 5F),
    Point3D(-10F, 10F, 5F),

    Point3D(-10F, 0F, 5F),
    Point3D(10F, 0F, 5F)
)

@Composable
fun Envelope3D(
    modifier: Modifier,
    progress: Float,
    onTopGlobalCoordinate: (Offset) -> Unit
) {

    Box (
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .width(150.dp)
                .height(75.dp)
        ) {
            drawPath(
                path = coordinates3DAsPath(coordinates = bottomCoordinates),
                color = Color(0xFF009999)
            )
        }

        Canvas(
            modifier = Modifier
                .width(150.dp)
                .height(75.dp)
                .onGloballyPositioned {
                    onTopGlobalCoordinate.invoke(it.positionInRoot())
                }
        ) {
            drawPath(
                path = coordinates3DAsPath(coordinates = topCoordinates),
                color = Color(0xFF00BBBB)
            )
        }
    }


    val sliderValue = progress * 5F
    val zDelta = abs(sliderValue / 10F) - 1
    topCoordinates[0].y = sliderValue
    topCoordinates[0].z = 5F + zDelta * 2

//            topCoordinates[1].y = sliderValue
//            topCoordinates[1].z = 5F + zDelta * 2

//    var sliderValue by remember { mutableStateOf(-5F) }
//    Slider(
//        value = sliderValue,
//        valueRange = -5F..5F,
//        onValueChange = {
//            sliderValue = it
//            val zDelta = abs(sliderValue / 10F) - 1
//            topCoordinates[0].y = sliderValue
//            topCoordinates[0].z = 5F + zDelta * 2
//
////            topCoordinates[1].y = sliderValue
////            topCoordinates[1].z = 5F + zDelta * 2
//        })

}

//@Composable
//@Preview (showBackground = true)
//fun DemoUI(modifier: Modifier = Modifier) {
//    Column (
//        modifier = Modifier.padding(24.dp)
//    ) {
//        Text("Hello there!", fontSize = 48.sp, fontWeight = FontWeight.Bold)
//        Spacer(modifier = Modifier.height(64.dp))
//        Text("Yours, Farid.", fontSize = 48.sp)
//    }
//}

//@Preview (showBackground = true)
//@Composable
//private fun DemoUIBendPrev() {
//
//    val graphicsLayer = rememberGraphicsLayer()
//    var resultingBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
//
//    Box (
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Box (
//            modifier = Modifier
//                .alpha(0F)
//                .drawWithContent {
//                    graphicsLayer.record {
//                        this@drawWithContent.drawContent()
//                    }
//                    drawLayer(graphicsLayer)
//                }
//                .border(width = 1.dp, color = Color.Black)
//
//        ) {
//            DemoUI()
//        }
//    }
//
//    resultingBitmap?.let {
//        Image(
//            bitmap = it,
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//
//    LaunchedEffect(Unit) {
//        delay(100)
//        val capturedBitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
//            .copy(Bitmap.Config.ARGB_8888, true)
//
//        val newBitmap = Bitmap.createBitmap(capturedBitmap.width * 2, capturedBitmap.height * 2, Bitmap.Config.ARGB_8888)
//
//
//        // code to transform bitmap to 3D coordinates
//        for (y in 0 until capturedBitmap.height) {
//            for (x in 0 until capturedBitmap.width) {
//                val pixel = capturedBitmap.getPixel(x, y)
//
//                val newZ : Float = if (y < capturedBitmap.height / 2) {
//                    0.75F
//                } else {
//                    1F
//                }
//                val newX = (x * 1F / newZ).toInt()
//                val newY = (y * 1F / newZ).toInt()
////                val coordinateZ = if (y < capturedBitmap.height / 2) 0F else 0F
////                val coordinate2D = Point3D(x.toFloat(), y.toFloat(), 1F).to2D(distance = 5F, capturedBitmap.width.toFloat(), capturedBitmap.height.toFloat())
//
//                newBitmap.setPixel(newX, capturedBitmap.height + newY, pixel)
//            }
//        }
//        // end
//        resultingBitmap = newBitmap.asImageBitmap()
//    }
//}


fun DrawScope.coordinates3DAsPath(
    coordinates: List<Point3D>,
    distance: Float = 100F,
): Path {
    val path = Path().apply {
        val firstCoordinate2D =
            coordinates[0].to2D(distance, size.width / 2, 0F)
        moveTo(firstCoordinate2D.x, firstCoordinate2D.y)

        for (i in 1..coordinates.lastIndex) {
            val point2D =
                coordinates[i].to2D(distance, size.width / 2, 0F)

            lineTo(
                x = point2D.x,
                y = point2D.y
            )
        }
    }
    return path
}

//class ComposeStateWithAnimation (
//    initialValue: Float,
//    animationDuration: Int
//) {
//    var value
//}

@Preview(showBackground = true)
@Composable
private fun Practice3DPrev() {
    val animationDuration = 1000
    var envelopeOffset by remember { mutableStateOf(1500F) }

    var messageOffset by remember { mutableStateOf(0F) }
    val animatedMessageOffset by animateFloatAsState(
        targetValue = messageOffset,
        animationSpec = tween(animationDuration)
    )
    var envelopeTopCoordinate by remember { mutableStateOf(Offset.Zero) }
    var textFieldTopCoordinate by remember { mutableStateOf(Offset.Zero) }
    var envelopeProgress by remember { mutableStateOf(-1F) }
    val animatedEnvelopeProgress by animateFloatAsState(
        targetValue = envelopeProgress,
        animationSpec = tween(animationDuration * 2)
    )
    var textFieldOffset by remember { mutableStateOf(0F) }
    val animatedTextFieldOffset by animateFloatAsState(
        targetValue = textFieldOffset,
        animationSpec = tween(animationDuration)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var text by remember { mutableStateOf("") }
        var isSent by remember { mutableStateOf(false) }
        var textFieldWidth by remember { mutableStateOf(0F) }
        var textFieldHeight by remember { mutableStateOf(0F) }
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()


        val textFieldWidthScale by animateFloatAsState(
            targetValue = if (isSent) (density.run { 150.dp.toPx() } / textFieldWidth) else 1F,
            animationSpec = tween(animationDuration)
        )
        val textFieldHeightScale by animateFloatAsState(
            targetValue = if (isSent) (density.run { 75.dp.toPx() } / textFieldHeight) else 1F,
            animationSpec = tween(animationDuration)
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .graphicsLayer {
                    scaleX = textFieldWidthScale
                    scaleY = textFieldHeightScale
                    translationY = animatedTextFieldOffset - ((1 - textFieldHeightScale) * textFieldHeight / 2) // - 100.dp.toPx()
                    translationX = animatedMessageOffset
                }
                .onGloballyPositioned {
                    textFieldTopCoordinate = it.positionInRoot()
                    textFieldWidth = it.size.width.toFloat()
                    textFieldHeight = it.size.height.toFloat()
                },
            value = text,
            placeholder = {
                Text(
                    text = "Type your message here...",
                    fontFamily = FontFamily(
                        Font(R.font.lobster_regular, weight = FontWeight.Normal)
                    ),
                    fontSize = 24.sp
                )
            },
            onValueChange = {
                text = it
            },
            textStyle = TextStyle(
                fontFamily = FontFamily(
                    Font(R.font.lobster_regular, weight = FontWeight.Normal)
                ),
                fontSize = 24.sp
            ),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(80.dp))

        Button(
            modifier = Modifier
                .graphicsLayer {
                    alpha = if (isSent) 0F else 1F
                },
            onClick = {
                scope.launch {
                    isSent = true
                    delay(animationDuration.toLong())
                    envelopeOffset = 0F
                    delay(animationDuration.toLong() / 3)
                    textFieldOffset = textFieldTopCoordinate.y - envelopeTopCoordinate.y - density.run { 100.dp.toPx() }
                    delay(animationDuration.toLong())
                    envelopeProgress = 1F
                    delay(animationDuration.toLong())
                    envelopeOffset = 1000F
                    messageOffset = 1000F
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                text = "Send",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(120.dp))

        val envelopeAnimatedOffset by animateFloatAsState(
            targetValue = envelopeOffset,
            animationSpec = tween(animationDuration)
        )

        Envelope3D(
            modifier = Modifier
                .graphicsLayer {
                    translationX = envelopeAnimatedOffset
                },
            progress = animatedEnvelopeProgress,
            onTopGlobalCoordinate = {
                textFieldTopCoordinate = it
            }
        )
    }
}