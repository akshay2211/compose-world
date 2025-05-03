package compose.world.composables.custom_lazy_columns

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.composables.shrink_layout.DemoInstaCommentItem
import compose.world.composables.shrink_layout.instagramDummyData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Author: Farid Guliyev
 * Date: [May 3, 2025]
 *
 * Description:
 * A fully customizable LazyColumn modification that adds pull-to-refresh functionality
 * in an iOS-style manner. The entire list can be dragged downward to trigger
 * a refresh gesture, providing a smooth and intuitive UX similar to iOS apps.
 *
 * For more, you can visit: https://github.com/faridGuliyew/compose-world
 */


enum class RefreshCommand {
    FORCE_REFRESH, CANCEL_REFRESH
}
class LazyColumnWithRefreshState {
    private var handlerCallback: ((RefreshCommand) -> Unit)? = null
    fun sendCommand(command: RefreshCommand) {
        handlerCallback?.invoke(command)
    }
    fun registerCallback(block: (RefreshCommand) -> Unit) {
        handlerCallback = block
    }
}

@Composable
fun rememberLazyColumnWithRefreshState() : LazyColumnWithRefreshState {
    return remember { LazyColumnWithRefreshState() }
}

@Composable
fun LazyColumnWithRefresh(
    modifier: Modifier = Modifier,
    refreshState: LazyColumnWithRefreshState = rememberLazyColumnWithRefreshState(),
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    maxRefreshDragAmountPx: Float = 500F,
    onRefresh: (progress: Float) -> Unit,
    refreshContent: @Composable (refreshProgress : Float, isRefreshing: Boolean) -> Unit,
    content: LazyListScope.() -> Unit,
) {
    var verticalTranslation by rememberSaveable { mutableFloatStateOf(0F) }
    val refreshProgress by rememberUpdatedState(verticalTranslation / maxRefreshDragAmountPx)
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    fun doRefresh(progress: Float = refreshProgress) {
        isRefreshing = true
        verticalTranslation = maxRefreshDragAmountPx / 2
        onRefresh(progress)
    }

    LaunchedEffect(Unit) {
        refreshState.registerCallback { refreshCommand ->
            when (refreshCommand) {
                RefreshCommand.FORCE_REFRESH -> {
                    doRefresh(progress = 1F)
                }
                RefreshCommand.CANCEL_REFRESH -> {
                    verticalTranslation = 0F
                    isRefreshing = false
                }
            }
        }
    }

    Box {

        /** REFRESH LAZY COLUMN */
        val animatedVerticalTranslation by animateFloatAsState(
            targetValue = verticalTranslation,
            animationSpec = spring(stiffness = Spring.StiffnessHigh),
            label = "LazyColumnRefreshTranslation"
        )

        LazyColumn(
            modifier = modifier
                .graphicsLayer { translationY = animatedVerticalTranslation }
                .nestedScroll(
                    remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(
                                available: Offset,
                                source: NestedScrollSource,
                            ): Offset {

                                val firstVisibleItem = state.layoutInfo.visibleItemsInfo.getOrNull(0)
                                val isLazyColumnAtInitialState = firstVisibleItem?.index == 0 && firstVisibleItem.offset == 0
                                val isUserScrollingDownwards = available.y > 0
                                val isLazyColumnDraggedUpwards = verticalTranslation > 0F

                                if (!isRefreshing && ((isLazyColumnAtInitialState && isUserScrollingDownwards) || isLazyColumnDraggedUpwards)) {
                                    val isTryingToRefresh = available.y > 0

                                    if (isTryingToRefresh) {
                                        val dragPortionToConsume =
                                            (maxRefreshDragAmountPx - verticalTranslation) / maxRefreshDragAmountPx
                                        verticalTranslation += available.y * dragPortionToConsume
                                    } else {
                                        verticalTranslation += available.y
                                    }

                                    return available
                                } else {
                                    return super.onPreScroll(available, source)
                                }
                            }

                            override suspend fun onPreFling(available: Velocity): Velocity {
                                if (verticalTranslation > 0F && !isRefreshing) {
                                    doRefresh()
                                    return available
                                }
                                return Velocity.Zero
                            }
                        }
                    }),
            state = state,
            contentPadding = contentPadding,
            flingBehavior = flingBehavior,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            reverseLayout = reverseLayout,
            userScrollEnabled = userScrollEnabled,
            content = content
        )

        /** Refresh content */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = if (!isRefreshing) verticalTranslation / 2
                    else maxRefreshDragAmountPx / 8
                },
            contentAlignment = Alignment.Center
        ) {
            refreshContent(refreshProgress, isRefreshing)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyColumnWithRefreshPrev() {
    val scope = rememberCoroutineScope()
    val refreshState = rememberLazyColumnWithRefreshState()
    val context = LocalContext.current

    var items by remember {
        mutableStateOf(
            instagramDummyData
        )
    }

    LazyColumnWithRefresh (
        modifier = Modifier,
        refreshState = refreshState,
        onRefresh = { progress->
            println("onRefresh called with $progress")
            if (progress < 0.7F) {
                refreshState.sendCommand(RefreshCommand.CANCEL_REFRESH)
                return@LazyColumnWithRefresh
            }

            scope.launch {
                delay(3000)
                items = instagramDummyData.shuffled()
                refreshState.sendCommand(RefreshCommand.CANCEL_REFRESH)
                Toast.makeText(context, "Refreshed!", Toast.LENGTH_SHORT).show()
            }
        },
        refreshContent = { progress, isRefreshing ->
            val animatedFontSize by animateFloatAsState(
                targetValue = 24 * progress
            )

            if (isRefreshing) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                Text(
                    modifier = Modifier.graphicsLayer {
                        alpha = progress
                    },
                    text = if (progress < 0.7F) "Pull down to refresh" else "Release to refresh!",
                    fontSize = animatedFontSize.sp,
                    color = Color.White
                )
            }
        }
    ) {
        items(items = items) {
            DemoInstaCommentItem(it)
        }
    }
}