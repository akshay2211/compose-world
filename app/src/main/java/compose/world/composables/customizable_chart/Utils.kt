package compose.world.composables.customizable_chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

fun List<Float>.mapToChartPoints() = this
    .mapWithPreviousIndexed(ChartPoint(values[0], 0F, 0F)) { prevPoint, value, index ->
        if (index == 0) ChartPoint(value, 0F, 0F)
        else {
            val delta = prevPoint.value - value
            ChartPoint(
                value = value,
                delta = delta,
                yCoordinate = (prevPoint.yCoordinate + delta)
            )
        }
    }

fun <T, R> List<T>.mapWithPreviousIndexed(
    initial: R,
    transform: (previous: R, current: T, index: Int) -> R
): List<R> {
    val result = mutableListOf<R>()
    var previous = initial
    for (i in this.indices) {
        val element = this[i]
        val mapped = transform(previous, element, i)
        result.add(mapped)
        previous = mapped
    }
    return result
}

@Composable
fun getScreenWidth(): Float {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidth = density.run { config.screenWidthDp.dp.toPx() }
    return screenWidth
}