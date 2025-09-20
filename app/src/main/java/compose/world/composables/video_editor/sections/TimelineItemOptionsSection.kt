package compose.world.composables.video_editor.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R
import compose.world.composables.video_editor.BgBlackLight

@Composable
fun ColumnScope.TimelineItemOptionsSection(
    selectedItem: TimelineItem? = null,
    onOptionSelected: (TimelineItemType, TimelineItemOption) -> Unit
) {
    AnimatedVisibility(visible = selectedItem != null) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = BgBlackLight)
                .navigationBarsPadding()
                .padding(vertical = 12.dp)
        ) {
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(start = 32.dp)
            ) {
                items((timelineGeneralItemOptionsFront + (selectedItem?.type?.options.orEmpty()) + timelineGeneralItemOptionsEnd).toList()) {
                    ItemOption(
                        label = it.label,
                        icon = it.icon,
                        onClick = { selectedItem?.type?.run { onOptionSelected(this, it) } }
                    )
                }
            }
        }
    }
}

enum class TimelineItemOption (
    val label: String,
    val icon: Int
) {
    // General options
    SPLIT("Split", R.drawable.ic_cut),
    DUPLICATE("Duplicate", R.drawable.ic_duplicate),
    DELETE("Delete", R.drawable.ic_delete),
    // Specific options (or shared between multiple, not all)
    STYLE("Style", R.drawable.ic_text),
    VOLUME("Volume", R.drawable.ic_volume),
}

val timelineGeneralItemOptionsFront = setOf(
    TimelineItemOption.SPLIT
)
val timelineGeneralItemOptionsEnd = setOf(
    TimelineItemOption.DUPLICATE, TimelineItemOption.DELETE
)
val timelineGeneralItemOptions = timelineGeneralItemOptionsFront + timelineGeneralItemOptionsEnd
val timelineTextItemOptions = setOf(
    TimelineItemOption.STYLE
)
val timelineAudioItemOptions = setOf(
    TimelineItemOption.VOLUME
)

val timelineVideoItemOptions = setOf(
    TimelineItemOption.VOLUME
)

val TimelineItemType.options get() = when(this) {
    TimelineItemType.TEXT -> timelineTextItemOptions
    TimelineItemType.VIDEO -> timelineVideoItemOptions
    TimelineItemType.AUDIO -> timelineAudioItemOptions
}

@Composable
fun ItemOption(
    label: String,
    icon: Int,
    onClick: () -> Unit
) {
    Column (
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null,
            tint = Color.White
        )
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Normal)
    }
}

@Preview
@Composable
private fun TimelineItemOptionsSectionPreview() {
    Column {
        TimelineItemOptionsSection(selectedItem = null, onOptionSelected = {_,_->})
    }
}