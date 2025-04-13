package compose.world.composables.motion_like_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MotionLikeScreen() {

    var dragOffset by remember { mutableFloatStateOf(0F) }
    var timeLimitShrinkBy by remember { mutableFloatStateOf(0F) }

    var topPartHeight by remember { mutableIntStateOf(0) } // Height of Əsas səhifə + Balans
    var timeLimitBottomPartHeight by remember { mutableIntStateOf(0) } // Height of Vaxt limiti

    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = dragOffset
            }
    ) {
        Column(
            modifier = Modifier.onSizeChanged { topPartHeight = it.height }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = Color.Red)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = Color.Blue)
            )

        }


        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .drawWithContent {
                    clipRect(bottom = size.height - timeLimitShrinkBy) {
                        this@drawWithContent.drawContent()
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = Color.Black)
            )
            HorizontalDivider()
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Red)
                .onSizeChanged {
                    timeLimitBottomPartHeight = it.height
                })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Blue)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.TopCenter)
                    .width(60.dp)
                    .height(5.dp)
                    .background(color = Color.Black)
            )


            val density = LocalDensity.current
            val state = rememberLazyListState()
            LaunchedEffect(state.isScrollInProgress) {
                if (state.isScrollInProgress) return@LaunchedEffect

                val isAtVeryVeryTop = dragOffset != 0F && state.firstVisibleItemIndex == 0
                if (isAtVeryVeryTop) {
                    state.scrollToItem(0, density.run { 20.dp.roundToPx() })
                }
            }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        if (state.firstVisibleItemIndex > 1 || (state.canScrollBackward && dragOffset == 0F)) return super.onPreScroll(available, source)

                        val newDragOffset = dragOffset + available.y
                        dragOffset = newDragOffset.coerceIn(
                            minimumValue = -(topPartHeight + timeLimitBottomPartHeight - density.run { 32.dp.toPx() }).toFloat(),
                            maximumValue = 0F
                        )

                        timeLimitShrinkBy =
                            (timeLimitBottomPartHeight - dragOffset - topPartHeight).coerceIn(
                                0F,
                                timeLimitBottomPartHeight.toFloat()
                            )

                        return if (timeLimitShrinkBy != timeLimitBottomPartHeight.toFloat()) available else super.onPreScroll(available, source)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.nestedScroll(
                    connection = nestedScrollConnection
                ),
                state = state
            ) {
                items(count = 100) {
                    Text(
                        text = "ITEM: $it",
                        color = Color.White,
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MotionLikeScreenPrev() {
    MotionLikeScreen()
}