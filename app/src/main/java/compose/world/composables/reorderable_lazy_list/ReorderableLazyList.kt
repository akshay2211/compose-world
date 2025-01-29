package compose.world.composables.reorderable_lazy_list

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * This module provides a reorderable lazy list implemented with Jetpack Compose.
 * The primary functionality allows users to drag and reorder items within the list,
 * using a custom stateful data structure ([ReorderableCollection]).
 *
 * ### Key Features:
 * - Supports drag-and-drop reordering of items.
 * - Customizable animation for item placement.
 * - Works with any data type through generic typing.
 *
 * ### Components:
 * - [ReorderableCollection]: A stateful interface for managing list items.
 * - [ReorderableLazyList]: A composable for rendering the reorderable list with animations.
 * - [rememberReorderableCollection]: A helper function for creating a reorderable collection.
 *
 * ### Example Usage:
 * ```kotlin
 * @Preview
 * @Composable
 * private fun ReorderableLazyListUsageExample() {
 *     ReorderableLazyList(
 *         data = rememberReorderableCollection(
 *             collection = listOf(1, 2, 3, 4, 5),
 *             toArray = { toTypedArray() }
 *         ),
 *         content = {
 *             Text(
 *                 modifier = Modifier
 *                     .fillMaxWidth()
 *                     .background(color = Color.Red)
 *                     .padding(12.dp),
 *                 text = "THIS IS $it",
 *                 color = Color.White,
 *                 fontWeight = FontWeight.Bold
 *             )
 *         }
 *     )
 * }
 * ```
 */


@Composable
fun <T> ReorderableLazyList(
    modifier: Modifier = Modifier,
    data: ReorderableCollection<T>,
    orderAnimation: FiniteAnimationSpec<IntOffset>? = spring(),
    content: @Composable (T) -> Unit
) {
    val items by data.items.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var draggedItem : LazyListItemInfo? = remember { null }
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggedItemDragAmount by remember { mutableFloatStateOf(0f) }

    LazyColumn (
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { dragStartCoordinate ->
                        draggedItem = listState.getDraggedItemByOffset(dragStartCoordinate.y.toInt())
                        draggedItemIndex = draggedItem?.index
                    },
                    onDrag = { _, dragAmountPerFrame ->
                        draggedItemDragAmount += dragAmountPerFrame.y

                        val _draggedItem = draggedItem ?: return@detectDragGesturesAfterLongPress
                        // Reorder logic
                        if (dragAmountPerFrame.y > 0) {
                            // If drag is positive, we will possibly reorder it with the next item
                            val itemBelowDraggedItem = listState.layoutInfo.visibleItemsInfo.getOrNull(_draggedItem.index + 1) ?: return@detectDragGesturesAfterLongPress
                            val shouldReorderWithBelowItem = draggedItem!!.offset + draggedItemDragAmount > itemBelowDraggedItem.offset

                            if (shouldReorderWithBelowItem) {
                                // Save removed item data, swap & restore removed item
                                val itemDataBelowDraggedItem = data[_draggedItem.index + 1]


                                draggedItem = itemBelowDraggedItem
                                data.removeAt(itemBelowDraggedItem.index)
                                data.add(itemBelowDraggedItem.index - 1, itemDataBelowDraggedItem)

                                draggedItemIndex = draggedItemIndex?.plus(1)
                                draggedItemDragAmount = 0F
                            }
                        }
                        else if (dragAmountPerFrame.y < 0) {
                            // If drag is negative, we will possibly reorder it with the previous item
                            val itemAboveDraggedItem = listState.layoutInfo.visibleItemsInfo.getOrNull(_draggedItem.index - 1) ?: return@detectDragGesturesAfterLongPress
                            val shouldReorderWithAboveItem = draggedItem!!.offset + draggedItemDragAmount < itemAboveDraggedItem.offset

                            if (shouldReorderWithAboveItem) {
                                // Save removed item data, swap & restore removed item
                                val itemDataAboveDraggedItem = data[_draggedItem.index - 1]


                                draggedItem = itemAboveDraggedItem
                                data.removeAt(itemAboveDraggedItem.index)
                                data.add(itemAboveDraggedItem.index + 1, itemDataAboveDraggedItem)

                                draggedItemIndex = draggedItemIndex?.minus(1)
                                draggedItemDragAmount = 0F
                            }
                        }
                    },
                    onDragEnd = {
                        draggedItemDragAmount = 0f
                        draggedItemIndex = null
                    },
                    onDragCancel = {
                        draggedItemDragAmount = 0f
                        draggedItemIndex = null
                    }
                )
            },
        state = listState,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        itemsIndexed(items = items, key = data.key) { index, item ->
            val dragModifier = if (index == draggedItemIndex) {
                Modifier
                    .zIndex(10f)
                    .graphicsLayer { translationY = draggedItemDragAmount }
            } else Modifier.animateItem(placementSpec = orderAnimation)

            Box (
                modifier = dragModifier
            ) {
                content(item)
            }
        }
    }
}

@Composable
fun <T: Any> rememberReorderableCollection(
    collection: List<T>,
    toArray: List<T>.() -> Array<T>,
    key: (Int, T) -> Any = { _, item-> item.hashCode() }
) : ReorderableCollection<T> {
    val collectionFlow = remember { MutableStateFlow(collection) }
    return remember {
        object : ReorderableCollection <T> {
            override val items: StateFlow<List<T>>
                get() = collectionFlow.asStateFlow()

            override val key: (Int, T) -> Any = key

            override fun removeAt(index: Int) {
                val oldList = collectionFlow.value
                val newList = mutableListOf(*toArray(oldList)).apply { removeAt(index) }
                collectionFlow.update { newList }
            }

            override fun get(index: Int): T {
                return collectionFlow.value[index]
            }

            override fun add(addIndex: Int, item: T) {
                val oldList = collectionFlow.value
                val newList = mutableListOf(*toArray(oldList)).apply { add(addIndex, item) }
                collectionFlow.update { newList }
            }
        }
    }
}

@Preview
@Composable
private fun ReorderableLazyListUsageExample() {
    ReorderableLazyList(
        data = rememberReorderableCollection(
            collection = listOf(1,2,3,4,5),
            toArray = { toTypedArray() }
        ),
        content = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Red)
                    .padding(12.dp),
                text = "THIS IS $it",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    )
}

fun LazyListState.getDraggedItemByOffset(
    dragStartCoordinate : Int
) : LazyListItemInfo? {
    val draggedItemInfo = layoutInfo.visibleItemsInfo.firstOrNull { itemInfo->
        val isItemInDragRange = dragStartCoordinate in (itemInfo.offset ..itemInfo.offset + itemInfo.size)
        isItemInDragRange
    }
    return draggedItemInfo
}