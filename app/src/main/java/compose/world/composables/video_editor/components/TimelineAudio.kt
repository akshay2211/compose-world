package compose.world.composables.video_editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.composables.video_editor.getWidthForDuration
import compose.world.composables.video_editor.sections.TimelineAudio
import compose.world.composables.video_editor.LightGray
import compose.world.composables.video_editor.ShapeSmall
import compose.world.composables.video_editor.toDp

@Composable
fun TimelineAudio(
    modifier: Modifier = Modifier,
    audio: TimelineAudio,
    measurer: TextMeasurer,
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
        offset = audio.offsetMillis,
        isSelected = isSelected,
        onDragItem = onDragItem,
        onDragLeft = onDragLeft,
        onDragRight = onDragRight,
        onChangeVerticalOrderBy = onChangeVerticalOrderBy,
        onClick = onToggleSelect,
        onDragEnd = onDragEnd
    ) {
        val textHeight = remember { measurer.measure(audio.name).size.height }
        val width = getWidthForDuration(audio.currentDurationInMillis)
        Text(
            modifier = Modifier
                .width(width)
                .background(color = Color(0xFF335053), shape = ShapeSmall)
                .border(width = 0.5.dp, color = LightGray, shape = ShapeSmall)
                .padding(vertical = 6.dp)
                .padding(start = 8.dp)
                .height(textHeight.toDp()),
            text = audio.name,
            color = Color.White,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun TimelineAudioPrev() {
//    TimelineAudio(
//        audio = TimelineAudio(
//            name = "Everette Griffith",
//            res = "alia",
//            timelineStartMillis = 3255,
//            timelineEndMillis = 1947,
//            verticalLayerIndex = 7922,
//            offsetMillis = 3685,
//            lengthInMillis = 4084
//        ),
//        isSelected = true,
//        onToggleSelect = {},
//        onDragItem = {},
//        onDragLeft = {},
//        onDragRight = {},
//        onChangeVerticalOrderBy = {},
//        offset = 100L
//    )
}