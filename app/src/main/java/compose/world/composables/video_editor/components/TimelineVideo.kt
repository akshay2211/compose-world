package compose.world.composables.video_editor.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.composables.video_editor.getWidthForDuration
import compose.world.composables.video_editor.sections.TimelineVideo
import compose.world.composables.video_editor.LightGray

@Composable
fun TimelineVideo(
    modifier: Modifier = Modifier,
    video: TimelineVideo,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onDragItem: (Long) -> Unit,
    onDragRight: (Long) -> Unit,
    onDragLeft: (Long) -> Unit,
    onChangeVerticalOrderBy: (Int) -> Unit,
    onDragEnd: () -> Unit
) {
    GenericTimelineItem(
        modifier = modifier,
        isSelected = isSelected,
        offset = video.offsetMillis,
        onDragItem = onDragItem,
        onDragLeft = onDragLeft,
        onDragRight = onDragRight,
        onChangeVerticalOrderBy = onChangeVerticalOrderBy,
        onClick = onToggleSelect,
        onDragEnd = onDragEnd
    ) {
        val width = getWidthForDuration(video.currentDurationInMillis)
        Row(
            modifier = Modifier
                .width(width)
                .height(50.dp)
                .border(width = 0.5.dp, color = LightGray),
            verticalAlignment = Alignment.CenterVertically
        ) {
            video.framesRes.forEach {
                Image(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(it),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )
            }
        }
    }
}

@Preview
@Composable
private fun TimelineVideoPrev() {
    TimelineVideo(
        video = TimelineVideo(
            res = "habitant",
            framesRes = listOf(),
            timelineStartMillis = 9464,
            timelineEndMillis = 6341,
            verticalLayerIndex = 9474,
            offsetMillis = 5394,
            lengthInMillis = 8235
        ),
        onToggleSelect = {},
        onDragItem = {},
        isSelected = true,
        onDragRight = {},
        onDragLeft = {},
        onChangeVerticalOrderBy = {},
        onDragEnd = {}
    )
}