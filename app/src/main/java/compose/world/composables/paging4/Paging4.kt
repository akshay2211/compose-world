package compose.world.composables.paging4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val PAGING_SIZE = 20

@Composable
fun Paging4() {
    var items by remember {
        mutableStateOf(listOf(1,2,3,4,5, 6, 7))
    }
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    val layoutInfo = remember { derivedStateOf { state.layoutInfo } }
    var isPagingLoading by remember { mutableStateOf(false) }
    LaunchedEffect(layoutInfo.value.visibleItemsInfo) {
        val lastItemIndex = layoutInfo.value.visibleItemsInfo.lastOrNull()?.index

        if (isPagingLoading) return@LaunchedEffect
        if (items.lastIndex == lastItemIndex) {
            scope.launch {
                println("MADE A REQUEST!")
                isPagingLoading = true
                delay(2000)
                val newItems = mutableListOf<Int>()
                for (i in (1..7)) {
                    newItems.add(items.last() + i)
                }
                items = items + newItems
                isPagingLoading = false
            }
        }
    }

    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) return@LaunchedEffect


    }

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        state = state
    ) {
        items(items) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Red)
                    .padding(12.dp),
                text = "Item $it"
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun Paging4Prev() {
    Paging4()
}