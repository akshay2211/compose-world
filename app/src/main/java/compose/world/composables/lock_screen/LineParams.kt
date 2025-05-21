package compose.world.composables.lock_screen

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class LineParams (
    val color: Color = Color.Black,
    val strokeWidth: Dp = 2.dp,
    val cap: StrokeCap = Stroke.DefaultCap,
    val pathEffect: PathEffect? = null,
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float = 1.0f,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DefaultBlendMode
)