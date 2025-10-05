package compose.world.composables.practice_3d

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@Composable
//fun Envelope3D_V2(
//    modifier: Modifier,
//    progress: Float,
//    onTopGlobalCoordinate: (Offset) -> Unit
//) {
//
//    Box(
//        modifier = modifier
//    ) {
//        Canvas(
//            modifier = Modifier
//                .width(150.dp)
//                .height(75.dp)
//        ) {
//            drawPath(
//                path = coordinates3DAsPath(coordinates = bottomCoordinates),
//                color = Color(0xFF009999)
//            )
//        }
//
//        Canvas(
//            modifier = Modifier
//                .width(150.dp)
//                .height(75.dp)
//                .onGloballyPositioned {
//                    onTopGlobalCoordinate.invoke(it.positionInRoot())
//                }
//        ) {
//            drawPath(
//                path = coordinates3DAsPath(coordinates = topCoordinates),
//                color = Color(0xFF00BBBB)
//            )
//        }
//    }
////
////
////    val sliderValue = progress * 5F
////    val zDelta = abs(sliderValue / 10F) - 1
////    topCoordinates[0].y = sliderValue
////    topCoordinates[0].z = 5F + zDelta * 2
//}

@Preview(showBackground = true)
@Composable
fun Practice3DPrevV2() {
    val animationDuration = 1000
    var envelopeOffset by remember { mutableStateOf(1500F) }

    var messageOffset by remember { mutableStateOf(0F) }
    val animatedMessageOffset by animateFloatAsState(
        targetValue = messageOffset, animationSpec = tween(animationDuration)
    )
    var envelopeTopCoordinate by remember { mutableStateOf(Offset.Zero) }
    var textFieldTopCoordinate by remember { mutableStateOf(Offset.Zero) }
    var envelopeProgress by remember { mutableStateOf(-1F) }
    var textFieldOffset by remember { mutableStateOf(0F) }
    val animatedTextFieldOffset by animateFloatAsState(
        targetValue = textFieldOffset, animationSpec = tween(animationDuration)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var text by remember { mutableStateOf("") }
        var isSent by remember { mutableStateOf(false) }
        var textFieldWidth by remember { mutableStateOf(0F) }
        var textFieldHeight by remember { mutableStateOf(0F) }
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()


        val textFieldWidthScale by animateFloatAsState(targetValue = if (isSent) (density.run { 150.dp.toPx() } / textFieldWidth) else 1F,
            animationSpec = tween(animationDuration))
        val textFieldHeightScale by animateFloatAsState(targetValue = if (isSent) (density.run { 75.dp.toPx() } / textFieldHeight) else 1F,
            animationSpec = tween(animationDuration))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .graphicsLayer {
                    scaleX = textFieldWidthScale
                    scaleY = textFieldHeightScale
                    translationY =
                        animatedTextFieldOffset - ((1 - textFieldHeightScale) * textFieldHeight / 2) // - 100.dp.toPx()
                    translationX = animatedMessageOffset
                }
                .onGloballyPositioned {
                    textFieldTopCoordinate = it.positionInRoot()
                    textFieldWidth = it.size.width.toFloat()
                    textFieldHeight = it.size.height.toFloat()
                }, value = text, placeholder = {
            Text(
                text = "Type your message here...", fontFamily = FontFamily(
                    Font(R.font.lobster_regular, weight = FontWeight.Normal)
                ), fontSize = 24.sp
            )
        }, onValueChange = {
            text = it
        }, textStyle = TextStyle(
            fontFamily = FontFamily(
                Font(R.font.lobster_regular, weight = FontWeight.Normal)
            ), fontSize = 24.sp
        ), colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
        )

//        Spacer(modifier = Modifier.height(80.dp))

        Button(modifier = Modifier
            .padding(top = 80.dp)
            .graphicsLayer {
                alpha = if (isSent) 0F else 1F
            }, onClick = {
            scope.launch {
                isSent = true
                delay(animationDuration.toLong())
                envelopeOffset = 0F
                delay(animationDuration.toLong() / 3)
                textFieldOffset =
                    textFieldTopCoordinate.y - envelopeTopCoordinate.y - density.run { 100.dp.toPx() }
                delay(animationDuration.toLong())
                envelopeProgress = 1F
                delay(animationDuration.toLong())
                envelopeOffset = 1000F
                messageOffset = 1000F
            }
        }) {
            Text(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                text = "Send",
                fontSize = 16.sp
            )
        }

//        Spacer(modifier = Modifier.height(120.dp))

        val envelopeAnimatedOffset by animateFloatAsState(
            targetValue = envelopeOffset, animationSpec = tween(animationDuration)
        )


        Canvas(modifier = Modifier
            .align { size, space, layoutDirection ->
                IntOffset(
                    y = 0, x = (space.width - size.width) / 2
                )
            }
            .graphicsLayer {
                translationX = envelopeAnimatedOffset
            }
            .padding(top = 120.dp)
            .width(150.dp)
            .height(75.dp)) {
            drawPath(
                path = coordinates3DAsPath(coordinates = bottomCoordinates),
                color = Color(0xFF009999)
            )
        }

        Canvas(modifier = Modifier
            .align { size, space, layoutDirection ->
                IntOffset(
                    y = 0, x = (space.width - size.width) / 2
                )
            }
            .graphicsLayer {
                translationX = envelopeAnimatedOffset
            }
            .padding(top = 120.dp)
            .width(150.dp)
            .height(75.dp)
            .onGloballyPositioned {
//                    onTopGlobalCoordinate.invoke(it.positionInRoot())
            }) {
            drawPath(
                path = coordinates3DAsPath(coordinates = topCoordinates), color = Color(0xFF00BBBB)
            )
        }
    }
}