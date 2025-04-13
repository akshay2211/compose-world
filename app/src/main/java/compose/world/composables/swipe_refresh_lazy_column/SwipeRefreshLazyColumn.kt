package compose.world.composables.swipe_refresh_lazy_column

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SwipeRefreshLazyColumn() {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .background(color = Color.Red)
                .padding(12.dp),
            text = "FARIDGRAM",
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        val lazyState = rememberLazyListState()
        var lazyColumnOffset by remember { mutableStateOf(0F) }
        val animatedLazyOffset by animateFloatAsState(lazyColumnOffset)
        val isLazyColumnAtTop by remember {
            derivedStateOf {
                lazyState.firstVisibleItemScrollOffset == 0
            }
        }
        var scrollingDownwards by remember {
            mutableStateOf(true)
        }

        LaunchedEffect(lazyState.isScrollInProgress) {
            if (!lazyState.isScrollInProgress && isLazyColumnAtTop) {
                scrollingDownwards = true
            }
        }

        Box {
            LazyColumn(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = animatedLazyOffset
                    },
                state = lazyState
            ) {
                items(50) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Blue)
                            .border(width = 2.dp, color = Color.Black)
                            .padding(12.dp),
                        text = "Post $it",
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(0.3F))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                lazyColumnOffset = 0F
                            }
                        ) { _, dragAmount ->
                            if (lazyState.firstVisibleItemScrollOffset == 0 && dragAmount.y > 0F) {
                                lazyColumnOffset += dragAmount.y
                                return@detectDragGestures
                            }

                            scope.launch {
                                lazyState.scrollBy(-dragAmount.y)
                            }
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeRefreshLazyColumnPrev() {
    SwipeRefreshLazyColumn()
}