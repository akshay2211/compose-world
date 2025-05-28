package compose.world.composables.picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularPicker(
    items: List<String>,
    itemHeight: Dp = 60.dp,
    visibleItemCount: Int = 10,
    onItemSelected: (index: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    // Calculate the index of the selected item based on scroll position
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex  }
            .collect { index ->
                selectedItemIndex = index.coerceIn(0, items.lastIndex)
                onItemSelected(selectedItemIndex)
            }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItemCount)
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
    ) {
        val animatedSelectedItemIndex by animateFloatAsState(
            targetValue = selectedItemIndex.toFloat(),
            animationSpec = tween(2000)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .nestedScroll(object : NestedScrollConnection {
                    override suspend fun onPreFling(available: Velocity): Velocity {
                        return available
                    }
                })
            ,
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight * ((visibleItemCount - 1) / 2) + itemHeight / 2),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            val firstVisibleItemScrollOffset by derivedStateOf { listState.firstVisibleItemScrollOffset }
            itemsIndexed(items = items) { index, item ->
                val distanceFromCenter = animatedSelectedItemIndex - index + firstVisibleItemScrollOffset / LocalDensity.current.run { itemHeight.toPx() } // + if (isSelectedItem) 0F else (firstVisibleItemScrollOffset / LocalDensity.current.run { itemHeight.toPx() })
//                val rotation by animateFloatAsState(
//                    targetValue = (distanceFromCenter * 15F).coerceAtMost(45F),
//                    animationSpec = tween(durationMillis = 1),
//                    label = "rotation"
//                )

                Box (
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .graphicsLayer {
                                val rotation = (distanceFromCenter * 15F) //.coerceAtMost(45F)
//                                if (index == selectedItemIndex) return@graphicsLayer
                                
                                rotationZ = -rotation
                                translationX = -rotation * 2 * distanceFromCenter
//                                translationY = distanceFromCenter * 20
                            }
                            .fillMaxWidth(),
                        text = item,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.Center)
                .background(Color.Gray.copy(alpha = 0.2f))
        )
    }
}

@Preview
@Composable
fun CircularPickerPrev() {
    // Sample data for the picker
    val items = (1..20).map { "Option $it" }
    var selectedItem by remember { mutableStateOf(items[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Selected: $selectedItem",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
        CircularPicker(
            items = items,
            onItemSelected = { selectedItem = items[it] }
        )
    }
}