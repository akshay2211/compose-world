package compose.world.composables.lock_screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.Dp

fun PointerInputScope.findDotHorizontalIndex(
    dragCoordinate: Offset,
    dotSize: Dp,
    rowPadding: Dp,
    dotSelectionErrorRadius: Dp,
): Int? {

    val sectionWidth = (dotSize + rowPadding).toPx()
    val sectionCountToTheLeft = (dragCoordinate.x / sectionWidth).toInt()

    // Check if it is in the range of left dot
    val leftDotRange = (sectionWidth * sectionCountToTheLeft)
        .run {
            val leftmost = this - dotSelectionErrorRadius.toPx()
            val rightmost = this + dotSize.toPx() + dotSelectionErrorRadius.toPx()

            return@run leftmost..rightmost
        }

    val rightDotRange = (sectionWidth * (sectionCountToTheLeft + 1))
        .run {
            val leftmost = this - dotSelectionErrorRadius.toPx()
            val rightmost = this + dotSize.toPx() + dotSelectionErrorRadius.toPx()

            return@run leftmost..rightmost
        }

    if (dragCoordinate.x in leftDotRange) {
        return sectionCountToTheLeft
    }

    if (dragCoordinate.x in rightDotRange) {
        return sectionCountToTheLeft + 1
    }

    return null
}

fun PointerInputScope.findDotVerticalIndex(
    dragCoordinate: Offset,
    dotSize: Dp,
    columnPadding: Dp,
    dotSelectionErrorRadius: Dp,
): Int? {

    val sectionHeight = (dotSize + columnPadding).toPx()
    val sectionCountAbove = (dragCoordinate.y / sectionHeight).toInt()

    // Check if it is in the range of left dot
    val aboveDotRange = (sectionHeight * sectionCountAbove)
        .run {
            val topmost = this - dotSelectionErrorRadius.toPx()
            val downmost = this + dotSize.toPx() + dotSelectionErrorRadius.toPx()

            return@run topmost..downmost
        }

    val belowDotRange = (sectionHeight * (sectionCountAbove + 1))
        .run {
            val topmost = this - dotSelectionErrorRadius.toPx()
            val downmost = this + dotSize.toPx() + dotSelectionErrorRadius.toPx()

            return@run topmost..downmost
        }

    if (dragCoordinate.y in aboveDotRange) {
        return sectionCountAbove
    }

    if (dragCoordinate.y in belowDotRange) {
        return sectionCountAbove + 1
    }

    return null
}