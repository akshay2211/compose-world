package compose.world.composables.reorderable_lazy_list_v3

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.zIndex

/**
 * A utility for handling reordering of items in a LazyColumn list.
 *
 * ## Usage Example:
 * ```kotlin
 * @Preview
 * @Composable
 * private fun ReorderableLazyListUsageExample() {
 *     class MyData (data: String) {
 *         var data by mutableStateOf(data)
 *     }
 *     val items = remember {
 *         listOf(
 *             ReorderableItem(MyData("1"), 0),
 *             ReorderableItem(MyData("2"), 1),
 *             ReorderableItem(MyData("3"), 2),
 *             ReorderableItem(MyData("4"), 3)
 *         )
 *     }
 *     ReorderableLazyList(
 *         items = items,
 *         content = { _, item ->
 *             TextField(
 *                 modifier = Modifier
 *                     .fillMaxWidth()
 *                     .background(color = Color.Red)
 *                     .padding(12.dp),
 *                 value = item.data,
 *                 onValueChange = {
 *                     item.data = it
 *                 },
 *                 singleLine = true
 *             )
 *         }
 *     )
 * }
 * ```
 *
 * ## Components:
 * - `ReorderableItem<T>`: Represents an item in a reorderable list, with properties for tracking drag state.
 * - `ReorderableLazyList<T>`: A composable that renders a list where items can be dragged and reordered.
 * - `ReorderDirection`: Enum representing the possible drag directions (UP or DOWN).
 * - Helper functions (`swapItemsDownwards`, `swapItemsUpwards`, `moveAboveItemToItsPosition`, `moveBelowItemToItsPosition`) handle item movement logic.
 *
 * ## Features:
 * - Supports drag gestures to rearrange items dynamically.
 * - Implements snapping behavior to maintain consistent item positioning.
 * - Uses artificial indices to track and adjust order during dragging.
 *
 * @author Farid G.
 */


@Composable
fun <T> ReorderableLazyList(
    modifier: Modifier = Modifier,
    items: List<ReorderableItem<T>>,
    content: @Composable (listIndex: Int, data: T, order: Int) -> Unit
) {
    var draggedItemListIndex by remember { mutableStateOf<Int?>(null) }
    var lastDraggedItemListIndex by remember { mutableStateOf<Int?>(null) }
    var itemSize = remember { 0 }

    Column(
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            val zIndexModifier = (if (index == draggedItemListIndex) {
                Modifier.zIndex(10f)
            } else Modifier)

            val animatedDrag by animateFloatAsState(item.dragAmount.toFloat())

            val dragModifier = Modifier
                .then(zIndexModifier)
                .graphicsLayer {
                    translationY = if (draggedItemListIndex == null) animatedDrag else item.dragAmount.toFloat()
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            lastDraggedItemListIndex = index
                            draggedItemListIndex = index
                        },
                        onDrag = { _, dragAmountPerFrame ->
                            item.dragBy(amount = dragAmountPerFrame.y.toInt())
                            println("BUG - dragging index: ${item.lazyListIndex}, artificial index: ${item.artificialIndex}")

                            if (dragAmountPerFrame.y > 0) {
                                val downwardsDrag = dragAmountPerFrame.y.toInt()

                                // Before dragging next item, check if there was an ongoing drag event in the opposite direction previously
                                val itemAbove =
                                    items.find { it.artificialIndex == item.artificialIndex - 1 }
                                moveAboveItemToItsPosition(
                                    aboveItem = itemAbove,
                                    downwardsDrag = downwardsDrag
                                ) ?: return@detectDragGestures

                                // There is no ongoing drag event in the opposite direction, proceed
                                val itemBelowDraggedItem =
                                    items.find { it.artificialIndex == item.artificialIndex + 1 }
                                        ?: return@detectDragGestures
                                itemBelowDraggedItem.dragBy(amount = -downwardsDrag)

                                val shouldReorderWithBelowItem =
                                    item.artificialDragAmount >= itemSize
                                if (shouldReorderWithBelowItem) swapItemsDownwards(
                                    item,
                                    itemBelowDraggedItem,
                                    itemSize
                                )

                            } else if (dragAmountPerFrame.y < 0) {
                                val upwardsDrag = dragAmountPerFrame.y.toInt()

                                // Before dragging next item, check if there was an ongoing drag event in the opposite direction previously
                                val itemBelow =
                                    items.find { it.artificialIndex == item.artificialIndex + 1 }
                                moveBelowItemToItsPosition(
                                    belowItem = itemBelow,
                                    upwardsDrag = upwardsDrag
                                ) ?: return@detectDragGestures

                                // There is no ongoing drag event in the opposite direction, proceed
                                val itemAboveDraggedItem =
                                    items.find { it.artificialIndex == item.artificialIndex - 1 }
                                        ?: return@detectDragGestures
                                itemAboveDraggedItem.dragBy(-upwardsDrag)

                                val shouldReorderWithAboveItem =
                                    item.artificialDragAmount <= -itemSize
                                if (shouldReorderWithAboveItem) swapItemsUpwards(
                                    item,
                                    itemAboveDraggedItem,
                                    itemSize
                                )
                            }
                        },
                        onDragEnd = {
                            // Algorithm to ACTUALLY reorder items
                            draggedItemListIndex = null
                            items.forEach {
                                it.lazyListIndex
                                it.snapToClosestIndex(itemSize)
                            }
                        },
                        onDragCancel = {
                            draggedItemListIndex = null
                            items.forEach {
                                it.snapToClosestIndex(itemSize)
                            }
                        }
                    )
                }

            Box(
                modifier = Modifier
                    .onSizeChanged {
                        itemSize = it.height
                    }.then(dragModifier)
            ) {
                content(index, item.data, item.artificialIndex)
            }
        }
    }
}

@Composable
fun <T> rememberReorderableLazyListItems(
    items: List<T>,
    cannotGoBelowIndex: Int = 0,
    cannotGoAboveIndex: Int = items.lastIndex,
) : List<ReorderableItem<T>> {
    return remember (items.size) {
        items.mapIndexed { index, it ->
            ReorderableItem(
                data = it,
                lazyListIndex = index,
                cannotGoBelowIndex = cannotGoBelowIndex,
                cannotGoAboveIndex = cannotGoAboveIndex
            )
        }
    }
}