package compose.world.composables.video_editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.world.composables.video_editor.LightGray
import compose.world.composables.video_editor.ShapeSmall
import compose.world.composables.video_editor.WhitePrimary

@Composable
fun TimelineAddItem(text: String) {
    Row (
        modifier = Modifier
            .background(color = LightGray, shape = ShapeSmall)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = WhitePrimary
        )
        Text(
            modifier = Modifier,
            text = text,
            color = WhitePrimary
        )
    }
}