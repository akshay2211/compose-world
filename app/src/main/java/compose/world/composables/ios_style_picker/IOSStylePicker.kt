package compose.world.composables.ios_style_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IOSStylePicker(
    items: List<String>,
    itemHeight: Dp = 40.dp,
    visibleItemCount: Int = 5,
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
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight * ((visibleItemCount - 1) / 2)),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            itemsIndexed(items = items) { index, item ->
                val distanceFromCenter = selectedItemIndex - index
                val rotation by animateFloatAsState(
                    targetValue = (distanceFromCenter * 15F).coerceAtMost(45F),
                    animationSpec = tween(durationMillis = 300),
                    label = "rotation"
                )

                Box (
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .graphicsLayer {
                                rotationX = rotation
                            }
                            .fillMaxWidth(),
                        text = item,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Overlay to mimic iOS picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Color.White.copy(alpha = 0.7f))
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(Color.White.copy(alpha = 0.7f))
                .align(Alignment.BottomCenter)
        )
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
private fun IOSStylePickerPrev() {
    // Sample data for the picker
    val items = (1..20).map { "Option $it" }
    var selectedItem by remember { mutableStateOf(items[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Selected: $selectedItem",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
        IOSStylePicker(
            items = items,
            onItemSelected = { selectedItem = items[it] }
        )
    }
}