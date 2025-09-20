package compose.world.composables.video_editor.sections

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.composables.video_editor.WIDTH_PER_MILLISECOND_DP
import compose.world.composables.video_editor.WhitePrimary
import compose.world.composables.video_editor.toDp
import compose.world.composables.video_editor.toPx
import kotlin.math.ceil

@Composable
fun TimelineTimestampSection(
    modifier: Modifier = Modifier,
    maxDurationInMillis: Long,
    style: TextStyle = TextStyle(fontSize = 8.sp, color = WhitePrimary)
) {
    val textMeasurer = rememberTextMeasurer()
    val textSize = remember {
        textMeasurer.measure(
            text = "00:00", style = style
        ).size
    }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val widthOneSecondDp = ((WIDTH_PER_MILLISECOND_DP * 1000).toFloat()).dp
    val widthOneSecond = widthOneSecondDp.toPx()

    val seconds by remember(maxDurationInMillis) {
        mutableIntStateOf(
            ceil((maxDurationInMillis + 1) / 1000F).toInt()
        )
    }

    val timelineWidthDp = widthOneSecondDp * seconds

    Canvas(
        modifier = modifier
            .requiredWidth(timelineWidthDp)
            .height(textSize.height.toDp())
    ) {
        val offset = widthOneSecond * 0.5F

        translate(left = (timelineWidthDp - screenWidthDp).coerceAtLeast(0.dp).toPx() / 2F) {
            clipRect {
                (0..seconds).forEach {
                    val x = widthOneSecond * it
                    val mins = it / 60
                    val seconds = "${it - mins * 60}".padStart(length = 2, padChar = '0')
                    val formattedText = "$mins:$seconds"
                    val measuredText = textMeasurer.measure(
                        formattedText, style
                    )

                    drawText(
                        textLayoutResult = measuredText,
                        topLeft = Offset(x, 0F),
                    )

                    drawCircle(
                        color = WhitePrimary,
                        radius = 3F,
                        center = Offset(y = center.y, x = x + offset)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelineTimestampSectionPrev() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        TimelineTimestampSection(
            maxDurationInMillis = 10000L
        )
    }
}