package compose.world.composables.reorderable_lazy_list_v3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class ReorderableItem<T>(
    val data: T,
    val lazyListIndex: Int,
    private val cannotGoBelowIndex: Int = 0,
    private val cannotGoAboveIndex: Int = 3
) {
    var dragAmount by mutableIntStateOf(0)
    var artificialIndex = lazyListIndex
    var artificialDragAmount = 0

    private fun changeArtificialOrder(changeBy: Int) {
        artificialDragAmount = 0
        artificialIndex += changeBy
    }

    private fun changeArtificialOrderByDirection(direction: ReorderDirection) {
        changeArtificialOrder(changeBy = direction.change)
    }

    fun changeArtificialOrderByDirectionAndErrorCorrect(direction: ReorderDirection, itemSize: Int) {
        changeArtificialOrderByDirection(direction = direction)
        errorCorrectDrag(itemSize = itemSize)
    }

    private fun getSnapIndexChange(itemSize: Int) : Int {
        if (artificialDragAmount < 0 && artificialDragAmount <= -itemSize / 2) {
            return - 1
        }

        if (artificialDragAmount > 0 && artificialDragAmount >= itemSize / 2) {
            return 1
        }

        return 0
    }

    fun snapToClosestIndex(itemSize: Int) {
        val indexChange = getSnapIndexChange(itemSize = itemSize)
        changeArtificialOrder(changeBy = indexChange)
        errorCorrectDrag(itemSize)
    }

    fun errorCorrectDrag(itemSize: Int) {
        val correctDragAmountForIndex = (artificialIndex - lazyListIndex) * itemSize
        artificialDragAmount = 0
        dragAmount = correctDragAmountForIndex
    }


    fun dragBy(amount: Int) {
        val newArtificialDragAmount = artificialDragAmount + amount
        if (artificialIndex == cannotGoBelowIndex && newArtificialDragAmount < 0) {
            dragAmount += artificialDragAmount
            artificialDragAmount = 0
            return
        }
        if (artificialIndex == cannotGoAboveIndex && newArtificialDragAmount > 0) {
            dragAmount -= artificialDragAmount
            artificialDragAmount = 0
            return
        }

        dragAmount += amount
        artificialDragAmount += amount
    }
}