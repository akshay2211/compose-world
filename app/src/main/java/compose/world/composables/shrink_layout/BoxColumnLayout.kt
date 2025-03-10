package compose.world.composables.shrink_layout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun BoxColumnLayout(
    modifier: Modifier,
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val screenHeight = density.run {
        LocalConfiguration.current.screenHeightDp.dp.roundToPx() +
                WindowInsets.navigationBars.getBottom(this)
    }

    Layout(
        modifier = modifier,
        content = {
            topContent()
            bottomContent()
        }, measurePolicy = { measurables, constraints ->
            val bottomPlaceable = measurables.last().measure(constraints)
            val bottomHeight = bottomPlaceable.height
            // Ensure top content does not overlap the bottom
            val maxTopHeight = (screenHeight - bottomHeight).coerceAtLeast(0)
            val topPlaceable = measurables.first().measure(
                constraints.copy(maxHeight = maxTopHeight)
            )

            val layoutHeight = topPlaceable.height + bottomHeight
            layout(constraints.maxWidth, layoutHeight) {
                topPlaceable.place(0, 0)
                bottomPlaceable.place(0, topPlaceable.height)
            }
        }
    )
}

@Composable
fun ShrinkLayoutExample(
    primaryColor: Color = Color(0xFF212529),
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    val density = LocalDensity.current
    val dismissDrag = density.run { LocalConfiguration.current.screenHeightDp.dp.toPx() + 100.dp.toPx() }

    var topSectionDrag by remember { mutableFloatStateOf(dismissDrag) }
    val animatedTopDrag by animateFloatAsState(topSectionDrag, label = "top_drag")
    var bottomSectionDrag by remember { mutableFloatStateOf(dismissDrag) }
    var topHandleHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            topSectionDrag = 0F
            bottomSectionDrag = 0F
        } else {
            topSectionDrag = dismissDrag
            bottomSectionDrag = dismissDrag
        }
    }

    BoxColumnLayout(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        topContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isVisible) {
                            Modifier.pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        if (topSectionDrag < size.height * 0.2F) {
                                            topSectionDrag = 0F
                                            bottomSectionDrag = 0F
                                        } else {
                                            onDismiss()
                                        }
                                    }
                                ) { _, dragAmount ->
                                    val newDrag = topSectionDrag + dragAmount.y
                                    if (newDrag < 0F) return@detectDragGestures

                                    topSectionDrag = newDrag

                                    bottomSectionDrag =
                                        ((size.height - topHandleHeight) - animatedTopDrag).coerceAtMost(
                                            0F
                                        )
                                }
                            }
                        } else Modifier
                    )
                    .graphicsLayer {
                        translationY = animatedTopDrag
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(color = primaryColor),
                ) {
                    DemoInstaCommentsHandleContent {
                        topHandleHeight = it
                    }

                    LazyColumn {
                        items(items = instagramDummyData) {
                            DemoInstaCommentItem(it)
                        }
                    }
                }
            }
        }, bottomContent = {
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = abs(bottomSectionDrag)
                    }
                    .drawBehind {
                        drawRect(
                            color = Color(0xFF212529),
                            size = this.size * 2F
                        )
                    }
            ) {
                HorizontalDivider(thickness = 0.1.dp)
                Column (
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    EmojiPane()
                    Spacer(modifier = Modifier.height(6.dp))
                    AddCommentSection()
                }
            }
        }
    )
}