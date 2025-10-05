package compose.world.composables.circular_countdown

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularTimer(
    modifier: Modifier = Modifier,
    timerText: String,
    timerTextStyle: TextStyle = TextStyle(
        fontSize = 44.sp,
        fontWeight = FontWeight.Bold
    ),
    subText: String,
    subTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Gray
    ),
    @DrawableRes icon: Int = R.drawable.ic_pause,
    startAngle: Float = 120F,
    sweepAngle: Float = 360F - (startAngle - 90F) * 2,
    @FloatRange(0.0, 1.0) progress: Float,
    fillColor: Color,
    backgroundColor: Color,
    strokeWidth: Dp,
    onControlIconClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(220.dp)
            .drawBehind {
                drawArc(
                    color = backgroundColor,
                    startAngle,
                    sweepAngle,
                    false,
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )

                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(fillColor, fillColor.copy(0.83F), fillColor.copy(0.5F))),
                    startAngle,
                    progress * sweepAngle,
                    false,
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )


                val angleInDegrees = (progress * sweepAngle) + startAngle - 90.0
                val radius = (size.height / 2)
                val x = -(radius * sin(Math.toRadians(angleInDegrees))).toFloat() + (size.width / 2)
                val y = (radius * cos(Math.toRadians(angleInDegrees))).toFloat() + (size.height / 2)

                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = Offset(x,  y)
                )
            }
            .padding(top = 60.dp)
            .padding(horizontal = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier)
            Text(
                text = timerText,
                style = timerTextStyle
            )
            Text(
                text = subText,
                style = subTextStyle
            )

            Icon(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = size.height * 0.5F
                    }
                    .border(
                        width = 1.5.dp,
                        color = Color.Red,
                        shape = CircleShape
                    )
                    .padding(4.dp)
                    .clip(shape = CircleShape)
                    .clickable(onClick = onControlIconClick, interactionSource = null, indication = rememberRipple())
                    .background(
                        color = Color.Red
                    )
                    .padding(16.dp)
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = "pause",
                tint = Color.White
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun CircularCountdownPrev() {
    var timePast by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0F) }
    var isPaused by remember { mutableStateOf(true) }
    LaunchedEffect(isPaused) {
        if (!isPaused) {
            while (true) {
                delay(1000)
                timePast++
            }
        }
    }
    LaunchedEffect(isPaused) {
        if (!isPaused) {
            animate(
                initialValue = progress,
                targetValue = 1F,
                animationSpec = tween(
                    durationMillis = 20000 - timePast * 1000,
                    easing = LinearEasing
                )
            ) { value, _->
                progress = value
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularTimer(
            timerText = "0:${if (timePast < 10) "0$timePast" else "$timePast"}",
            subText = "Time past",
            progress = progress,
            fillColor = Color(0xFF005251),
            backgroundColor = Color(0xFFDBE4E4),
            strokeWidth = 16.dp,
            icon = if (isPaused) R.drawable.ic_play else R.drawable.ic_pause,
            onControlIconClick = {
                isPaused = !isPaused
            }
        )
    }
}