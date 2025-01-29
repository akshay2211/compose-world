package compose.world.composables.reorderable_lazy_list_v3

/**
 * Convenience function for moving dragged item down, below item up
 */
internal fun <T> swapItemsDownwards(
    firstItem: ReorderableItem<T>,
    secondItem: ReorderableItem<T>,
    itemSize: Int
) {
    firstItem.changeArtificialOrderByDirectionAndErrorCorrect(
        direction = ReorderDirection.DOWN,
        itemSize = itemSize
    )

    secondItem.changeArtificialOrderByDirectionAndErrorCorrect(
        direction = ReorderDirection.UP,
        itemSize = itemSize
    )
}

/**
 * Convenience function for moving dragged item up, above item down
 */
internal fun <T> swapItemsUpwards(
    firstItem: ReorderableItem<T>,
    secondItem: ReorderableItem<T>,
    itemSize: Int
) {
    firstItem.changeArtificialOrderByDirectionAndErrorCorrect(
        direction = ReorderDirection.UP,
        itemSize = itemSize
    )

    secondItem.changeArtificialOrderByDirectionAndErrorCorrect(
        direction = ReorderDirection.DOWN,
        itemSize = itemSize
    )
}

/**
 * Moves above item back to its intended place
 */
internal fun <T> moveAboveItemToItsPosition(aboveItem: ReorderableItem<T>?, downwardsDrag: Int) : Unit? {
    if (aboveItem == null || aboveItem.artificialDragAmount <= 0F) return Unit

    val newArtificialDragAmount = aboveItem.artificialDragAmount - downwardsDrag
    if (newArtificialDragAmount < 0F) {
        aboveItem.dragAmount -= aboveItem.artificialDragAmount
        aboveItem.artificialDragAmount = 0
    } else {
        aboveItem.dragAmount -= downwardsDrag
        aboveItem.artificialDragAmount = newArtificialDragAmount
    }

    return null
}

/**
 * Moves below item back to its intended place
 */
internal fun <T> moveBelowItemToItsPosition(belowItem: ReorderableItem<T>?, upwardsDrag: Int) : Unit? {
    if (belowItem == null || belowItem.artificialDragAmount >= 0F) return Unit

    val newArtificialDragAmount = belowItem.artificialDragAmount - upwardsDrag
    if (newArtificialDragAmount > 0F) {
        belowItem.dragAmount -= belowItem.artificialDragAmount
        belowItem.artificialDragAmount = 0
    } else {
        belowItem.dragAmount -= upwardsDrag
        belowItem.artificialDragAmount = newArtificialDragAmount
    }

    return null
}