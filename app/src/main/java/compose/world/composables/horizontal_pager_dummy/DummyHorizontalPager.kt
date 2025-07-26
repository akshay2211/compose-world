package compose.world.composables.horizontal_pager_dummy

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DummyPagerTest() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // PagerState for the foreground pager (e.g., main content)
        val foregroundPagerState = rememberPagerState(pageCount = { 3 })

        // PagerState for the background pager (e.g., background images)
        val backgroundPagerState = rememberPagerState(pageCount = { 3 })

        // Coroutine scope for launching scroll operations
        val coroutineScope = rememberCoroutineScope()

        // Synchronize foregroundPagerState to backgroundPagerState
        LaunchedEffect(foregroundPagerState) {
            snapshotFlow {
                foregroundPagerState.currentPage to foregroundPagerState.currentPageOffsetFraction
            }.collectLatest { (page, offset) ->
                // Only scroll the backgroundPager if the foregroundPager is being actively scrolled by the user
                // This prevents an infinite loop if both LaunchedEffects are active
                if (foregroundPagerState.isScrollInProgress) {
                    coroutineScope.launch {
                        backgroundPagerState.scrollToPage(page, offset)
                    }
                }
            }
        }

        // Synchronize backgroundPagerState to foregroundPagerState (optional, for bidirectional sync)
        LaunchedEffect(backgroundPagerState) {
            snapshotFlow {
                backgroundPagerState.currentPage to backgroundPagerState.currentPageOffsetFraction
            }.collectLatest { (page, offset) ->
                // Only scroll the foregroundPager if the backgroundPager is being actively scrolled by the user
                if (backgroundPagerState.isScrollInProgress) {
                    coroutineScope.launch {
                        foregroundPagerState.scrollToPage(page, offset)
                    }
                }
            }
        }



        HorizontalPager(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)),
            userScrollEnabled = !foregroundPagerState.isScrollInProgress,
            state = backgroundPagerState,
            verticalAlignment = Alignment.Top
        ) {
            when (it) {
                0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(color = Color.Red)
                    )
                }

                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
//                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                            .background(color = Color.Green)
                    )
                }

                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
//                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                            .background(color = Color.Blue)
                    )
                }
            }
        }

        HorizontalPager(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            state = foregroundPagerState,
            userScrollEnabled = !backgroundPagerState.isScrollInProgress,
            verticalAlignment = Alignment.Bottom
        ) {
            when (it) {
                0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(color = Color.Red)
                    )
                }

                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
//                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                            .background(color = Color.Green)
                    )
                }

                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
//                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                            .background(color = Color.Blue)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DummyPagerTestPrev() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DummyPagerTest()
    }
}