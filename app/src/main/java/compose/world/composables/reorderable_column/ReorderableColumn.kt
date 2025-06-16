package compose.world.composables.reorderable_column

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.abs

class ReorderableColumnItemDataWrapper <T> (val data: T)

class ReorderableColumnItem <T> (
    val realIndex: Int,
    dataWrapper : ReorderableColumnItemDataWrapper<T>
) {
    var index by mutableIntStateOf(realIndex)
    var offset by mutableFloatStateOf(0F)
    private var dataWrapper by mutableStateOf(dataWrapper)
    val data get() =  dataWrapper.data

    fun updateData(block: T.() -> T) {
        dataWrapper = ReorderableColumnItemDataWrapper(block(data))
    }

    fun updateIndex(newIndex: Int, itemHeight: Int) {
        index = newIndex
        updateOffsetForIndex(newIndex, itemHeight)
    }

    fun updateOffsetForOwnIndex(itemHeight: Int) {
        updateOffsetForIndex(index, itemHeight)
    }

    private fun updateOffsetForIndex(index: Int, itemHeight: Int) {
        offset = getOffsetForIndex(index, itemHeight)
    }

    private fun getOffsetForIndex(index: Int, itemHeight: Int) : Float {
        return ((index - realIndex) * itemHeight).toFloat()
    }
}

val items = listOf(
    ReorderableColumnItem(0, ReorderableColumnItemDataWrapper(TextItemForPrev("Text one"))),
    ReorderableColumnItem(1, ReorderableColumnItemDataWrapper(TextItemForPrev("Text two"))),
    ReorderableColumnItem(2, ReorderableColumnItemDataWrapper(TextItemForPrev("Text three"))),
)

@Composable
fun <T> ReorderableColumn(
    items: List<ReorderableColumnItem<T>>,
    content: @Composable (Modifier, ReorderableColumnItem<T>) -> Unit
) {
    var draggedItemRealIndex by remember { mutableIntStateOf(-1) }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            val animatedOffset by animateFloatAsState(
                targetValue = item.offset,
                animationSpec = spring()
            )

            ReorderableItem(
                modifier = Modifier
                    .zIndex(
                        zIndex = if (item.realIndex == draggedItemRealIndex) 10F else 1F
                    )
                    .graphicsLayer {
                        translationY = animatedOffset
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                draggedItemRealIndex = item.realIndex
                            },
                            onDragEnd = {
                                // Calculate actual offset for index
                                item.updateOffsetForOwnIndex(size.height)
                            }
                        ) { _, dragAmount ->
                            val verticalDragAmount = dragAmount.y

                            val realOffset = item.offset + verticalDragAmount
                            val offset = realOffset - ((item.index - item.realIndex) * size.height)
                            val movedIndexAmount = (offset / size.height).coerceIn(-1.5F, 1.5F) // Should not move by 2 indices at a time

                            val isFirstAndGoesUpOrIsLastAndGoesDown = item.index == 0 && offset <= 0 || item.index == items.lastIndex && offset >= 0
                            if (isFirstAndGoesUpOrIsLastAndGoesDown) {
                                return@detectDragGestures
                            }

                            item.offset = realOffset

                            if (abs(movedIndexAmount) >= 1.0) {
                                val newIndex = item.index + movedIndexAmount.toInt()
                                // Find item at that index & update its index
                                val itemToSwap = items.find { it.index == newIndex }
                                itemToSwap?.updateIndex(item.index, size.height)
                                // Update index of current item
                                item.index = newIndex
                            }
                        }
                    },
                item = item,
                content = content
            )
        }
    }
}

@Composable
fun <T> ReorderableItem(
    item: ReorderableColumnItem<T>,
    modifier: Modifier,
    content: @Composable (Modifier, ReorderableColumnItem<T>) -> Unit
) {
    content(modifier, item)
}

data class TextItemForPrev(
    val value : String
)

@Preview(showBackground = true)
@Composable
private fun ReorderableColumnPrev() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ReorderableColumn(
            items = items,
            content = { modifier, item->
                Row(
                    modifier = modifier
                        .padding(vertical = 2.dp)
                        .height(IntrinsicSize.Min)
                        .border(width = 2.dp, color = Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = item.data.value,
                        singleLine = true,
                        onValueChange = { newInput->
                            item.updateData { copy(newInput) }
                        }
                    )
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        text = "Drag"
                    )
                }
            }
        )
    }
}