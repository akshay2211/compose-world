package compose.world.composables.animated_balance_increase.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.composables.animated_balance_increase.LocalColorScheme
import compose.world.ui.theme.SoraFontFamily

@Composable
fun AddCashButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    val colorScheme = LocalColorScheme.current
    Row(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = null,
                indication = rememberRipple(color = colorScheme.onSecondary),
                onClick = onButtonClick
            )
            .background(color = colorScheme.secondary)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            imageVector = Icons.Default.Add,
            tint = colorScheme.onSecondary,
            contentDescription = "add"
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Add Cash",
            style = TextStyle(
                color = colorScheme.onSecondary,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                fontFamily = SoraFontFamily
            )
        )
    }
}