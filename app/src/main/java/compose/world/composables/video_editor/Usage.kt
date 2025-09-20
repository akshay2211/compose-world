package compose.world.composables.video_editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.R
import compose.world.composables.video_editor.components.PrimarySlider
import compose.world.composables.video_editor.sections.TimelineAudio
import compose.world.composables.video_editor.sections.TimelineItem
import compose.world.composables.video_editor.sections.TimelineItemOption
import compose.world.composables.video_editor.sections.TimelineItemOptionsSection
import compose.world.composables.video_editor.sections.TimelineItemType
import compose.world.composables.video_editor.sections.TimelineText
import compose.world.composables.video_editor.sections.TimelineVideo
import compose.world.composables.video_editor.sections.dragBy
import compose.world.composables.video_editor.sections.setEndMillisTo
import compose.world.composables.video_editor.sections.setLayerTo
import compose.world.composables.video_editor.sections.setStartMillisTo
import compose.world.composables.video_editor.sections.timelineGeneralItemOptions
import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Preview(showBackground = true)
@Composable
private fun TimelineUsage() {
    val texts = remember {
        mutableStateListOf(
            TimelineText(
                text = "My text",
                timelineStartMillis = 0L,
                timelineEndMillis = 1000L,
                verticalLayerIndex = 0,
                offsetMillis = 0L
            ),
            TimelineText(
                text = "new text",
                timelineStartMillis = 200L,
                timelineEndMillis = 1200L,
                verticalLayerIndex = 1,
                offsetMillis = 0L
            ),
        )
    }
    val audios = remember {

        mutableStateListOf(
            TimelineAudio(
                name = "Shakira - Waka Waka (This Time for Africa) (The Official 2010 FIFA World Cup (TM) Song)",
                res = "",
                timelineStartMillis = 0L,
                timelineEndMillis = 1.seconds.inWholeMilliseconds,
                offsetMillis = 0L,
                verticalLayerIndex = 0,
                lengthInMillis = 30.seconds.inWholeMilliseconds
            ),
            TimelineAudio(
                name = "All I Want For Christmas Is You - Mariah Carey",
                res = "",
                timelineStartMillis = 250L,
                timelineEndMillis = 1.2.seconds.inWholeMilliseconds,
                offsetMillis = 0L,
                verticalLayerIndex = 1,
                lengthInMillis = 80.seconds.inWholeMilliseconds
            )
        )
    }
    val videos = remember {
        mutableStateListOf(
            TimelineVideo(
                res = "",
                framesRes = listOf(
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                ),
                timelineStartMillis = 0L,
                timelineEndMillis = 1000L,
                verticalLayerIndex = 0,
                offsetMillis = 0L,
                lengthInMillis = 2000L
            ),
            TimelineVideo(
                res = "",
                framesRes = listOf(
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                    R.drawable.ic_launcher_background,
                ),
                timelineStartMillis = 300L,
                timelineEndMillis = 700L,
                verticalLayerIndex = 1,
                offsetMillis = 0L,
                lengthInMillis = 7000L
            )
        )
    }

    val maxDurationInMillis by remember { derivedStateOf {
        (videos + audios + texts).maxOfOrNull { it.timelineEndMillis } ?: 1L
    } }

    // These 2 values should be in sync manually (for performance reasons I did not infer one from another)
    var selectedTimelineItemIndex by remember { mutableStateOf<Int?>(null) }
    var selectedTimelineItemType by remember { mutableStateOf<TimelineItemType?>(null) }
    //
    val selectedTimelineItemsList by remember {
        derivedStateOf {
            when(selectedTimelineItemType) {
                TimelineItemType.TEXT -> texts as SnapshotStateList<TimelineItem>
                TimelineItemType.VIDEO -> videos as SnapshotStateList<TimelineItem>
                TimelineItemType.AUDIO -> audios as SnapshotStateList<TimelineItem>
                null -> mutableStateListOf()
            }
        }
    }
    val horizontalScrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().background(color = BgBlack)) {
        // Top part of the screen (above timeline)
        Column (modifier = Modifier.weight(1F)) {
            // Video preview
            Box (modifier = Modifier.fillMaxWidth().weight(1F)) {
                Image(
                    modifier = Modifier.fillMaxHeight()
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.thumbnail_demo),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )
            }

            // Video options (play/pause, full screen and etc.)
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = WhiteLight
                )

                PrimarySlider(
                    modifier = Modifier
                        .fillMaxWidth(0.1F)
                        .align(Alignment.BottomEnd),
                    initialProgress = 0.5F,
                    onProgressChanged = {
                        WIDTH_PER_MILLISECOND_DP = 0.1 * (it.coerceAtLeast(0.1F) * 10F) / 3F
                    }
                )
            }
        }

        Timeline(
            totalDurationInMillis = maxDurationInMillis,
            texts = texts,
            audios = audios,
            videos = videos,
            horizontalScrollState = horizontalScrollState,
            selectedTimelineItemMap = selectedTimelineItemIndex?.let {
                mapOf(
                    selectedTimelineItemType!! to it
                )
            }.orEmpty(),
            onToggleSelection = { type, index ->
                if (type == selectedTimelineItemType && index == selectedTimelineItemIndex) {
                    selectedTimelineItemIndex = null
                    return@Timeline
                }

                selectedTimelineItemIndex = index
                selectedTimelineItemType = type
            },
            onAddTimelineItem = {
                when (it) {
                    TimelineItemType.TEXT -> {
                        val biggestVerticalIndex = texts.maxOfOrNull { it.verticalLayerIndex } ?: -1
                        texts.add(
                            TimelineText(
                                text = "New text!",
                                timelineStartMillis = 0L,
                                timelineEndMillis = 3.seconds.inWholeMilliseconds,
                                verticalLayerIndex = biggestVerticalIndex + 1,
                                offsetMillis = 0L
                            )
                        )
                    }

                    TimelineItemType.VIDEO -> {
                        val biggestVerticalIndex =
                            videos.maxOfOrNull { it.verticalLayerIndex } ?: -1
                        videos.add(
                            TimelineVideo(
                                res = "",
                                framesRes = listOf(
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_background,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_foreground,
                                    R.drawable.ic_launcher_foreground,
                                ),
                                timelineStartMillis = 0L,
                                timelineEndMillis = 5.seconds.inWholeMilliseconds,
                                verticalLayerIndex = biggestVerticalIndex + 1,
                                offsetMillis = 0L,
                                lengthInMillis = 4500L
                            )
                        )
                    }

                    TimelineItemType.AUDIO -> {
                        val biggestVerticalIndex =
                            audios.maxOfOrNull { it.verticalLayerIndex } ?: -1
                        audios.add(
                            TimelineAudio(
                                res = "",
                                name = "Copy my flow - She said she from the islands",
                                timelineStartMillis = 0L,
                                timelineEndMillis = 10.seconds.inWholeMilliseconds,
                                offsetMillis = 0L,
                                verticalLayerIndex = biggestVerticalIndex + 1,
                                lengthInMillis = 2.minutes.inWholeMilliseconds
                            )
                        )
                    }
                }
            }
        )

        // Timeline options
        val selectedItem = selectedTimelineItemIndex?.run { selectedTimelineItemsList[this] }
        val widthPerMillisecondPx = WIDTH_PER_MILLISECOND_DP.dp.toPx()
        TimelineItemOptionsSection(
            selectedItem = selectedItem,
            onOptionSelected = { type, option ->
                // Handle general options
                if (option in timelineGeneralItemOptions) {
                    when (option) {
                        TimelineItemOption.SPLIT -> {
                            val playheadCurrentDurationInMillis = (horizontalScrollState.value / widthPerMillisecondPx).toLong()
                            val isPlayheadAtInvalidPoint = playheadCurrentDurationInMillis.let { it < selectedItem!!.timelineStartMillis || it > selectedItem.timelineEndMillis }
                            if (isPlayheadAtInvalidPoint) return@TimelineItemOptionsSection


                            // Shorten the first element.
                            selectedTimelineItemsList[selectedTimelineItemIndex!!] = selectedItem!!.setEndMillisTo(playheadCurrentDurationInMillis)
                            // Duplicate and match other part.
                            selectedTimelineItemsList.add(
                                selectedItem
                                    .setStartMillisTo(playheadCurrentDurationInMillis)
                                    .copy(id = UUID.randomUUID().toString())
                            )
                        }
                        TimelineItemOption.DUPLICATE -> {
                            val biggestVerticalIndex = selectedTimelineItemsList.maxOfOrNull { it.verticalLayerIndex } ?: -1

                            val copyItem = selectedItem!!
                                .dragBy(selectedItem.currentDurationInMillis)
                                .setLayerTo(biggestVerticalIndex + 1)
                                .copy(id = UUID.randomUUID().toString())

                            selectedTimelineItemsList.add(copyItem)
                        }
                        TimelineItemOption.DELETE -> {
                            val selectedIndex = selectedTimelineItemIndex!!
                            val selectedList = selectedTimelineItemsList
                            selectedTimelineItemIndex = null
                            selectedTimelineItemType = null
                            selectedList.removeAt(selectedIndex)
                        }
                        else -> Unit
                    }
                    return@TimelineItemOptionsSection
                }
                when (type) {
                    TimelineItemType.TEXT -> {
                        when (option) {
                            TimelineItemOption.STYLE -> error("Not yet impl.")
                            else -> error("Unsupported option $option for item $selectedTimelineItemType!!")
                        }
                    }
                    TimelineItemType.VIDEO -> {

                    }
                    TimelineItemType.AUDIO -> {

                    }
                }
            }
        )
    }
}