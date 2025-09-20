package compose.world.composables.video_editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.composables.video_editor.getWidthForDuration
import compose.world.composables.video_editor.LightGray
import compose.world.composables.video_editor.ShapeSmall
import compose.world.composables.video_editor.sections.TimelineText
import compose.world.composables.video_editor.toDp

@Composable
fun TimelineText(
    modifier: Modifier = Modifier,
    text: TimelineText,
    measurer: TextMeasurer,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onDragItem: (millis: Long) -> Unit,
    onDragRight: (Long) -> Unit,
    onDragLeft: (Long) -> Unit,
    onChangeVerticalOrderBy: (Int) -> Unit,
    onDragEnd: () -> Unit
) {
    val textHeight = remember(text) { measurer.measure(text.text).size.height }
    val width = getWidthForDuration(text.currentDurationInMillis)

    GenericTimelineItem(
        modifier = modifier,
        isSelected = isSelected,
        offset = text.offsetMillis,
        onDragItem = onDragItem,
        onDragLeft = onDragLeft,
        onDragRight = onDragRight,
        onChangeVerticalOrderBy = onChangeVerticalOrderBy,
        onClick = onToggleSelect,
        onDragEnd = onDragEnd
    ) {
        Text(
            modifier = Modifier
                .width(width)
                .background(color = Color(0xFFFF5722), shape = ShapeSmall)
                .border(width = 0.5.dp, color = LightGray, shape = ShapeSmall)
                .padding(vertical = 6.dp)
                .padding(start = 8.dp)
                .height(textHeight.toDp()),
            text = text.text,
            color = Color.White,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun TimelineTextPrev() {
    Box(modifier = Modifier.padding(12.dp)) {
        TimelineText(
            text = TimelineText(
                text = "regione",
                timelineStartMillis = 4692,
                timelineEndMillis = 6790,
                verticalLayerIndex = 1992,
                offsetMillis = 5096,
                lengthInMillis = 2439
            ),
            measurer = rememberTextMeasurer(),
            onToggleSelect = {},
            onDragItem = {},
            isSelected = false,
            onDragRight = {},
            onDragLeft = {},
            onChangeVerticalOrderBy = {},
            onDragEnd = {}
        )
    }
}