package compose.world.composables.animated_balance_increase.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import compose.world.composables.animated_balance_increase.LocalColorScheme

@Composable
fun MinimizeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colorScheme = LocalColorScheme.current
    Icon(
        modifier = modifier
            .clip(shape = CircleShape)
            .clickable(interactionSource = null, indication = rememberRipple(), onClick = onClick)
            .border(width = 1.dp, color = colorScheme.outline, shape = CircleShape)
            .background(color = colorScheme.primaryContainer)
            .padding(2.dp)
            .size(16.dp),
        imageVector = Icons.Default.Close,
        tint = colorScheme.secondary,
        contentDescription = "minimize"
    )
}