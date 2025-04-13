package compose.world

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview (showBackground = true)
fun Tasks() {

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Task 1
        var isActive : Boolean by remember { mutableStateOf(false) }
        val transition = updateTransition(isActive)
        val color by transition.animateColor { active->
            if (active) Color.Red else Color.Black
        }
        val size by transition.animateFloat { active->
            if (active) 200F else 100F
        }

        Column {
            Text(text = "Task 1", fontSize = 24.sp)
            Box(modifier = Modifier
                .size(size.dp)
                .background(color = color)
                .clickable { isActive = !isActive }
            )
        }

        Column {
            Text(text = "Task 2", fontSize = 24.sp)
            val infiniteRotation by rememberInfiniteTransition()
                .animateFloat(initialValue = 0F, targetValue = 360F, animationSpec = infiniteRepeatable(
                    tween(easing = LinearEasing, durationMillis = 500)
                ))
            Box(modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(color = Color.Black.copy(0.25F))
                .drawBehind {
                    rotate(degrees = infiniteRotation) {
                        drawLine(
                            color = Color.Red,
                            start = center,
                            end = Offset(x = this.size.width, y = this.center.y),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color = Color.Black))
            }
        }

        Column {
            Text(text = "Task 3", fontSize = 24.sp)
            val minHeight = 20.dp
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)
                .border(width = 1.dp, color = Color.Black)) {
                var offset by remember { mutableStateOf(0F) }
                val animatedOffset by animateFloatAsState(offset, animationSpec = tween())
                Column (
                    modifier = Modifier.fillMaxWidth()
                        .graphicsLayer {
                            translationY = animatedOffset
                        }
                        .height(200.dp)
                        .background(color = Color.Gray)
                        .padding(vertical = 12.dp, horizontal = 6.dp)
                        .pointerInput(Unit) {
                            detectDragGestures (
                                onDragEnd = {
                                    if (offset >= this.size.height / 2) {
                                        offset = (this.size.height - minHeight.toPx())
                                    } else {
                                        offset = 0F
                                    }
                                }
                            ) { change, dragAmount ->
                                val newOffset = offset + dragAmount.y

                                if (this.size.height - newOffset <= minHeight.toPx() || newOffset < 0F) {
                                    return@detectDragGestures
                                }

                                offset = newOffset

                            }
                        }
                ) {
                    Text(text = "This is a bottom sheet sample", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}