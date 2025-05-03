package compose.world.composables.custom_lazy_columns

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Author: Farid Guliyev
 * Date: [May 3, 2025]
 *
 * Description:
 * LazyColumn modification that adds overscroll functionality
 * in an iOS-style manner, providing a smooth and intuitive UX similar to iOS apps.
 *
 * For more, you can visit: https://github.com/faridGuliyew/compose-world
 */

@Composable
fun IosStyleLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    maxUpwardsDragAmountPx: Float = 500F,
    content: LazyListScope.() -> Unit,
) {
    var verticalTranslation by rememberSaveable { mutableFloatStateOf(0F) }
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
                            val isLazyColumnAtInitialState =
                                firstVisibleItem?.index == 0 && firstVisibleItem.offset == 0
                            val isUserScrollingDownwards = available.y > 0
                            val isLazyColumnAtRefreshState = verticalTranslation > 0F

                            if ((isLazyColumnAtInitialState && isUserScrollingDownwards) || isLazyColumnAtRefreshState) {
                                val isTryingToRefresh = available.y > 0

                                if (isTryingToRefresh) {
                                    val dragPortionToConsume =
                                        (maxUpwardsDragAmountPx - verticalTranslation) / maxUpwardsDragAmountPx
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
                            val shouldCancelUpwardsDrag = verticalTranslation > 0F
                            if (shouldCancelUpwardsDrag) {
                                verticalTranslation = 0F
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
}

@Preview (showBackground = true)
@Composable
private fun IosStyleLazyColumnPrev() {
    IosStyleLazyColumn {
        items(count = 100) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .border(width = 1.dp, color = Color.Black)
                    .padding(16.dp),
                text = "Item $it",
                style = TextStyle(
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}