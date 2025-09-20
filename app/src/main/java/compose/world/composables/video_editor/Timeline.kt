package compose.world.composables.video_editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import compose.world.R
import compose.world.composables.video_editor.components.PrefixIconWithoutExtraSpace
import compose.world.composables.video_editor.sections.TimelineAudio
import compose.world.composables.video_editor.sections.TimelineAudioSection
import compose.world.composables.video_editor.sections.TimelineItemType
import compose.world.composables.video_editor.sections.TimelineText
import compose.world.composables.video_editor.sections.TimelineTextSection
import compose.world.composables.video_editor.sections.TimelineTimestampSection
import compose.world.composables.video_editor.sections.TimelineVideo
import compose.world.composables.video_editor.sections.TimelineVideoSection
import kotlin.math.ceil

@Composable
fun Timeline(
    totalDurationInMillis: Long,
    texts: SnapshotStateList<TimelineText>,
    audios: SnapshotStateList<TimelineAudio>,
    videos: SnapshotStateList<TimelineVideo>,
    horizontalScrollState: ScrollState,
    selectedTimelineItemMap: Map<TimelineItemType, Int>,
    onToggleSelection: (TimelineItemType, Int) -> Unit,
    onAddTimelineItem: (TimelineItemType) -> Unit
) {
    val screenWidth = getScreenWidth()

    Column {
        val seconds by remember(totalDurationInMillis) { mutableIntStateOf(ceil((totalDurationInMillis + 1) / 1000F).toInt()) }
        val timelineWidth = getWidthForDuration(seconds * 1000L)

        TimelineTimestampSection(
            modifier = Modifier
                .width(timelineWidth)
                .graphicsLayer {
                translationX = (screenWidth / 2).toPx() + (24).dp.toPx() - horizontalScrollState.value
            },
            maxDurationInMillis = totalDurationInMillis
        )

        Column (
            modifier = Modifier
                .systemBarsPadding()
                .width(screenWidth)
                .height(300.dp)
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x + (24.dp.toPx()), 0F),
                        end = Offset(center.x + (24.dp.toPx()), size.height),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(rememberScrollState())
        ) {
            // Timestamp on top
            Row {
                Box(modifier = Modifier.width(timelineWidth))
                // Space for making sure timeline play head can reach the end.
                Box(modifier = Modifier.width(screenWidth))
            }

            Row {
                // First half of the screen (empty) - space for playhead to be able to reach the start
                Spacer(modifier = Modifier.width(screenWidth / 2))

                Spacer(modifier = Modifier.width(24.dp))

                // Second half of the screen (for displaying media sections)
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrefixIconWithoutExtraSpace(
                        icon = R.drawable.ic_video,
                        onClick = { onAddTimelineItem(TimelineItemType.VIDEO) }
                    ) {
                        TimelineVideoSection(
                            videos = videos,
                            selectedIndex = selectedTimelineItemMap[TimelineItemType.VIDEO],
                            onToggleSelection = { onToggleSelection(TimelineItemType.VIDEO, it) }
                        )
                    }

                    PrefixIconWithoutExtraSpace(
                        icon = R.drawable.ic_audio,
                        onClick = { onAddTimelineItem(TimelineItemType.AUDIO) }
                    ) {
                        TimelineAudioSection(
                            audios = audios,
                            selectedIndex = selectedTimelineItemMap[TimelineItemType.AUDIO],
                            onToggleSelection = { onToggleSelection(TimelineItemType.AUDIO, it) }
                        )
                    }

                    PrefixIconWithoutExtraSpace(
                        icon = R.drawable.ic_text,
                        onClick = { onAddTimelineItem(TimelineItemType.TEXT) }
                    ) {
                        TimelineTextSection(
                            texts = texts,
                            selectedIndex = selectedTimelineItemMap[TimelineItemType.TEXT],
                            onToggleSelection = { onToggleSelection(TimelineItemType.TEXT, it) }
                        )
                    }
                }
            }
        }
    }
}