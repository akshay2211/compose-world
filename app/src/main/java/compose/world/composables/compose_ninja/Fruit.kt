package compose.world.composables.compose_ninja

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class Fruit(
    val label: String,
    val cutInfos: List<CutInfo> = emptyList(),
    offset: Offset = Offset.Zero,
    val content: @Composable () -> Unit,
) {
    var translationX by mutableFloatStateOf(offset.x)
    var translationY by mutableFloatStateOf(offset.y)
    var size by mutableStateOf(Size.Zero)

    fun getTranslation(): Offset {
        return Offset(translationX, translationY)
    }

    fun copy(
        label: String = this.label,
        cutInfos: List<CutInfo> = this.cutInfos,
        isLeftSlice: Boolean,
        content: @Composable () -> Unit = this.content,
    ): Fruit {
        return Fruit(
            label = label,
            cutInfos = cutInfos,
            content = content
        ).also {
            it.translationX = this.translationX - if (isLeftSlice) 10F else -10F
            it.translationY = this.translationY
            it.size = this.size
        }
    }

    override fun toString(): String {
        return "label: ${label}, size: $size, x: $translationX, y: $translationY"
    }
}