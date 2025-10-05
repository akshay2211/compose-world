package compose.world.composables.motion_search

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMotionApi::class)
@Composable
fun MotionSearch(
    modifier: Modifier,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress, label = "motion_progress"
    )
    Box(modifier = Modifier.fillMaxSize()
        .background(color = Color.Black)) {
        MotionLayout(
            modifier = modifier,
            start = ConstraintSet {
                val topPart = createRefFor("top")
                val bottomPart = createRefFor("bottom")

                constrain(topPart) {
                    bottom.linkTo(parent.top)
//                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
//                    this.verticalBias = 0f
                    height = Dimension.wrapContent
                    width = Dimension.matchParent
                }

                constrain(bottomPart) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = 100.dp.asDimension()
                    width = Dimension.matchParent
                }
            },
            end = ConstraintSet {
                val topPart = createRefFor("top")
                val bottomPart = createRefFor("bottom")

                constrain(topPart) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.wrapContent
                    width = Dimension.matchParent
                }

                constrain(bottomPart) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(topPart.bottom)
                    height = Dimension.fillToConstraints
                    width = Dimension.matchParent
                }
            },
            progress = animatedProgress
        ) {
            Box(
                modifier = Modifier
                    .layoutId("top")
                    .background(color = Color.White)
                    .padding(
                        vertical = 12.dp
                    )
            ) {
                Column {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Search",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        value = "",
                        onValueChange = {},
                        label = {
                            Text("Enter an address")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults
                            .colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        value = "",
                        onValueChange = {},
                        label = {
                            Text("Enter an address")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults
                            .colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .layoutId("bottom")
                    .clip(RoundedCornerShape(
                        topStart = 16.dp * (1f - progress),
                        topEnd = 16.dp * (1f - progress)
                    ))
                    .background(color = Color.White)
            ) {
                if (progress < 1f) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp)
                            .alpha(1f - progress),
                        value = "",
                        onValueChange = {},
                        label = {
                            Text("Enter an address")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults
                            .colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                    )
                }
                if (progress > 0.5f) {
                    Text(
                        modifier = Modifier.padding(32.dp)
                            .alpha((progress - 0.5f) * 2),
                        text = "Search results",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                }
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun MotionSearchPrev() {
    var progress by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    val scope = rememberCoroutineScope()
    MotionSearch(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
            detectVerticalDragGestures(
                onDragEnd = {
                    scope.launch {
                        if (progress < 0.5f) {
                            progress = 0f
                        } else {
                            progress = 1f
                        }
                    }
                },
                onVerticalDrag = { _, dragAmount ->
                    scope.launch {
                        progress = (progress - dragAmount / 100f).coerceIn(0f, 1f)
                    }
                }
            )
        },
        progress = progress
    )
}