package compose.world.composables.draggable_column


import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * Represents the state of an individual draggable item in a column.
 *
 * This class manages the logical and visual state of a draggable item, including its position,
 * offset, and drag state. It is used together with the [DraggableContent] composable to
 * enable drag-and-drop reordering functionality.
 *
 * @param T The type of data associated with this draggable item.
 * @param index The initial index of the item in the list.
 * @param data The data object associated with the item.
 *
 * @property beforeDragOrder The item's order in the list before the drag operation starts.
 * @property offsetY The vertical offset of the item in pixels during a drag.
 * @property isBeingDragged Indicates whether the item is currently being dragged.
 */

class DraggableItemState<T>(
    index: Int, val data: T
) {
    // actual order - meaning before drag order of the element.
    var beforeDragOrder by mutableIntStateOf(index)
    private var logicalOrder by mutableIntStateOf(index)
    fun increaseLogicalOrder() {
        logicalOrder += 1
        if (!isBeingDragged) {
            beforeDragOrder = logicalOrder
        }
    }

    fun decreaseLogicalOrder() {
        logicalOrder -= 1
        if (!isBeingDragged) {
            beforeDragOrder = logicalOrder
        }
    }

    fun readLogicalOrder() = logicalOrder

    var offsetY by mutableIntStateOf(0)
    var isBeingDragged by mutableStateOf(false)
}

/**
 * Creates a list of [DraggableItemState] for the given items.
 */

@Composable
fun <T> rememberDraggableItemStates(
    items: List<T>
): List<DraggableItemState<T>> {
    return remember {
        items.mapIndexed { index, data ->
            DraggableItemState(index, data)
        }
    }
}

/**
 * A composable that creates a draggable vertical column of items.
 *
 * This layout allows items to be rearranged by dragging them. Items are animated smoothly
 * during dragging, and their positions are updated dynamically based on their drag state.
 *
 * @param T The type of data associated with each item.
 * @param itemHeightPx The height of each item in pixels.
 * @param draggableItems The list of draggable item states.
 * @param initialComposable A composable to render items in their normal state.
 * @param draggedComposable A composable to render the item being dragged.
 * @param dragRotation The rotation angle (in degrees) applied to the dragged item.
 * dragModifier: Use with elements that will consume drag events. visualModifier: Use with elements that will change visually with drag
 * @author Farid Guliyev
 *
 * @sample app.crocusoft.ui_toolkit.components.draggable.DraggableColumnPrev
 */

@Composable
fun <T> DraggableContent(
    itemHeightPx: Float,
    draggableItems: List<DraggableItemState<T>>,
    initialComposable: @Composable (visualModifier: Modifier, dragModifier: Modifier, state: DraggableItemState<T>) -> Unit,
    draggedComposable: @Composable (visualModifier: Modifier, state: DraggableItemState<T>) -> Unit,
    dragRotation: Float
) {
    DimensionSubcomposeLayout(
        mainContent = {
            Column {
                draggableItems.forEach {
                    initialComposable(Modifier, Modifier, it)
                }
            }
        }, dependentContent = {
            val density = LocalDensity.current
            var draggedState: DraggableItemState<T>? by remember {
                mutableStateOf(null)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // wrap content
                    .height(
                        height = density.run { it.toDpSize().height })
                    .border(color = Color.Black, width = 2.dp)) {
                draggableItems.forEach { state ->
                    val animatedYOffset by animateIntAsState((state.beforeDragOrder * itemHeightPx + state.offsetY).toInt())

                    initialComposable(
                        Modifier
                            .graphicsLayer {
                                alpha = if (state.isBeingDragged) 0f else 1f
                            }
                            .offset {
                                IntOffset(x = 0, y = animatedYOffset)
                            }, Modifier.pointerInput(itemHeightPx) {
                            detectVerticalDragGestures(onDragStart = {
                                state.isBeingDragged = true
                                state.beforeDragOrder = state.readLogicalOrder()
                                draggedState = state
                            }, onDragEnd = {
                                state.isBeingDragged = false
                                state.beforeDragOrder = state.readLogicalOrder()
                                state.offsetY = 0
                                draggedState = null
                            }) { _, dragAmount ->
                                state.offsetY += dragAmount.toInt()
                                swapOrderIfNeeded(
                                    itemHeightPx = itemHeightPx,
                                    draggedState = state,
                                    otherStates = draggableItems,
                                    thresholdPercentage = 0.5f
                                )
                            }
                        }, state
                    )
                }
                draggedState?.let {
                    draggedComposable(
                        Modifier
                            .zIndex(10f)
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = (it.beforeDragOrder * itemHeightPx + it.offsetY).toInt()
                                )
                            }
                            .graphicsLayer {
                                transformOrigin = TransformOrigin.Center
                                rotationZ = dragRotation
                            }, it
                    )
                }
            }
        }, placeMainContent = false
    )
}

/**
 * Adjusts the order of items in the column based on the dragged item's position.
 *
 * This function calculates whether the dragged item has crossed the threshold for swapping
 * positions with its neighboring items and updates the order accordingly.
 *
 * @param T The type of data associated with the items.
 * @param itemHeightPx The height of each item in pixels.
 * @param draggedState The state of the currently dragged item.
 * @param otherStates The list of states for all items in the column.
 * @param thresholdPercentage The threshold for swapping items, as a percentage of item height.
 */

fun <T> swapOrderIfNeeded(
    itemHeightPx: Float,
    draggedState: DraggableItemState<T>,
    otherStates: List<DraggableItemState<T>>,
    @FloatRange(0.0, 1.0) thresholdPercentage: Float = 0.5f
) {
    val currentLogicalOrder = draggedState.readLogicalOrder()
    // We need to calculate it based on its on drag start top position! Otherwise result will be inaccurate. Because, when order changes from 0 to 1, it is not actually arrived at order 1 yet, it still has some more way to go, so inaccurate result!
    val currentItemInitialTopPosition =
        draggedState.beforeDragOrder * itemHeightPx + draggedState.offsetY
    val nextItemTopPosition =
        (currentLogicalOrder + 1) * itemHeightPx + itemHeightPx * thresholdPercentage
    val prevItemTopPosition =
        (currentLogicalOrder - 1) * itemHeightPx + itemHeightPx * thresholdPercentage

    // CHECK IF ITEM EXCEED THE NEXT ONE'S TOP POSITION
    if (currentItemInitialTopPosition > nextItemTopPosition) {
        val nextState = otherStates.find { it.readLogicalOrder() == currentLogicalOrder + 1 }
        nextState?.let {
            draggedState.increaseLogicalOrder()
            it.decreaseLogicalOrder()
        }
    }
    // CHECK IF ITEM IS BELOW THE PREVIOUS ONE'S TOP POSITION
    else if (currentItemInitialTopPosition < prevItemTopPosition) {
        val prevState = otherStates.find { it.readLogicalOrder() == currentLogicalOrder - 1 }
        prevState?.let {
            draggedState.decreaseLogicalOrder()
            it.increaseLogicalOrder()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DraggableColumnPrev() {
    var contentHeight by remember {
        mutableFloatStateOf(1f)
    }
    LocalDensity.current
    Box(modifier = Modifier.fillMaxSize()) {
        DraggableContent(
            itemHeightPx = contentHeight,
            dragRotation = 0f,
            draggableItems = rememberDraggableItemStates(
                listOf(
                    "Hello",
                    "World",
                    "This",
                    "is",
                    "draggable",
                    "column"
                )
            ),
            initialComposable = { visualModifier, dragModifier, state ->
                Column(
                    modifier = visualModifier
                        .then(dragModifier)
                        .then(
                            Modifier.onSizeChanged {
                                contentHeight = it.height.toFloat()
                            })) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(color = Color.Red),
                        text = state.data,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            },
            draggedComposable = { modifier, state ->
                Column(
                    modifier = modifier
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(color = Color.Red),
                        text = state.data,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            })
    }
}