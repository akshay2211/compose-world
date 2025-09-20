package compose.world.composables.video_editor.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import compose.world.composables.video_editor.components.TimelineAddItem
import compose.world.composables.video_editor.components.TimelineText
import compose.world.composables.video_editor.WIDTH_PER_MILLISECOND_DP
import compose.world.composables.video_editor.toDp
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

class TimelineText(
    override val id: String = UUID.randomUUID().toString(),
    val text: String,
    override val timelineStartMillis: Long,
    override val timelineEndMillis: Long,
    override val verticalLayerIndex: Int,
    override val offsetMillis: Long,
    override val lengthInMillis: Long = 60.minutes.inWholeMilliseconds
) : TimelineItem {
    override fun copy(
        id: String,
        timelineStartMillis: Long,
        timelineEndMillis: Long,
        verticalLayerIndex: Int,
        offsetMillis: Long,
    ): TimelineText {
        return TimelineText(
            id = id,
            text = text,
            timelineStartMillis = timelineStartMillis,
            timelineEndMillis = timelineEndMillis,
            verticalLayerIndex = verticalLayerIndex,
            offsetMillis = offsetMillis,
            lengthInMillis = lengthInMillis
        )
    }
}

@Composable
fun TimelineTextSection(
    texts: SnapshotStateList<TimelineText>,
    selectedIndex: Int?,
    onToggleSelection: (Int) -> Unit
) {
    if (texts.isEmpty()) {
        TimelineAddItem(text = "Add text")
    } else {
        val measurer = rememberTextMeasurer()
        var itemHeight by remember { mutableIntStateOf(0) }

        Box {
            texts.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val updatedItem by rememberUpdatedState(item)
                Box {
                    TimelineText(
                        modifier = Modifier
                            .zIndex(if (isSelected) 1F else 0F)
                            .padding(top = (item.verticalLayerIndex * itemHeight).toDp())
                            .onSizeChanged { itemHeight = it.height }
                            .graphicsLayer {
                                translationX = item.timelineStartMillis * WIDTH_PER_MILLISECOND_DP.dp.toPx()
                                alpha = if (selectedIndex != null) {
                                    if (isSelected) 1F else 0.1F
                                } else 1F
                            },
                        text = item,
                        measurer = measurer,
                        isSelected = isSelected,
                        onToggleSelect = {
                            onToggleSelection(index)
                        },
                        onDragItem = {
                            (texts as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                                newItem = updatedItem.dragBy(it),
                                index = index
                            )
                        },
                        onDragLeft = {
                            (texts as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                                newItem = updatedItem.adjustStartMillisBy(it),
                                index = index
                            )
                        },
                        onDragRight = {
                            (texts as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                                newItem = updatedItem.adjustEndMillisBy(it),
                                index = index
                            )
                        },
                        onChangeVerticalOrderBy = {
                            (texts as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                                newItem = updatedItem.adjustLayerBy(it),
                                index = index
                            )
                        },
                        onDragEnd = {
                            (texts as SnapshotStateList<TimelineItem>).removeVerticalGaps()
                        }
                    )
                }
            }
        }
    }
}