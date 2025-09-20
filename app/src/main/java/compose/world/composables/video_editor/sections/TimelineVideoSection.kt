package compose.world.composables.video_editor.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import compose.world.composables.video_editor.components.TimelineAddItem
import compose.world.composables.video_editor.WIDTH_PER_MILLISECOND_DP
import compose.world.composables.video_editor.components.TimelineVideo
import java.util.UUID

class TimelineVideo(
    override val id: String = UUID.randomUUID().toString(),
    val res: String,
    val framesRes: List<Int>,
    override val timelineStartMillis: Long,
    override val timelineEndMillis: Long,
    override val verticalLayerIndex: Int,
    override val offsetMillis: Long,
    override val lengthInMillis: Long
) : TimelineItem {
    override fun copy(
        id: String,
        timelineStartMillis: Long,
        timelineEndMillis: Long,
        verticalLayerIndex: Int,
        offsetMillis: Long
    ): TimelineVideo {
        return TimelineVideo(
            id = id,
            res = res,
            framesRes = framesRes,
            timelineStartMillis = timelineStartMillis,
            timelineEndMillis = timelineEndMillis,
            verticalLayerIndex = verticalLayerIndex,
            offsetMillis = offsetMillis,
            lengthInMillis = lengthInMillis
        )
    }
}

@Composable
fun TimelineVideoSection(
    videos: SnapshotStateList<TimelineVideo>,
    selectedIndex: Int?,
    onToggleSelection: (Int) -> Unit
) {
    if (videos.isEmpty()) {
        TimelineAddItem(text = "Add video")
    } else {
        Box {
            videos.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val updatedItem by rememberUpdatedState(item)
                TimelineVideo(
                    modifier = Modifier
                        .zIndex(if (isSelected) 1F else 0F)
                        .padding(top = (item.verticalLayerIndex * 50).dp)
                        .graphicsLayer {
                            translationX =
                                item.timelineStartMillis * WIDTH_PER_MILLISECOND_DP.dp.toPx()
                            alpha = if (selectedIndex != null) {
                                if (isSelected) 1F else 0.1F
                            } else 1F
                        },
                    video = item,
                    isSelected = isSelected,
                    onToggleSelect = {
                        onToggleSelection(index)
                    },
                    onDragItem = {
                        (videos as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                            newItem = updatedItem.dragBy(it),
                            index = index
                        )
                    },
                    onDragLeft = {
                        (videos as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                            newItem = updatedItem.adjustStartMillisBy(it),
                            index = index
                        )
                    },
                    onDragRight = {
                        (videos as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                            newItem = updatedItem.adjustEndMillisBy(it),
                            index = index
                        )
                    },
                    onChangeVerticalOrderBy = {
                        (videos as SnapshotStateList<TimelineItem>).updateWithOverlapCheck(
                            newItem = updatedItem.adjustLayerBy(it),
                            index = index
                        )
                    },
                    onDragEnd = {
                        (videos as SnapshotStateList<TimelineItem>).removeVerticalGaps()
                    }
                )
            }
        }
    }
}