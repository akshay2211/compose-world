package compose.world.composables.animated_balance_increase.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.composables.animated_balance_increase.LocalColorScheme
import compose.world.composables.animated_balance_increase.ScreenColorScheme
import compose.world.ui.theme.SoraFontFamily

@Composable
fun PaymentItem(
    cardNumber: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = LocalColorScheme.current

    val radioColor by animateColorAsState(
        targetValue = if (selected) colorScheme.secondary else colorScheme.primaryContainer
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(interactionSource = null, indication = rememberRipple(), onClick = onClick)
            .background(color = colorScheme.primaryContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(width = 2.dp, color = colorScheme.secondary, shape = CircleShape)
                .padding(4.dp)
                .background(color = radioColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "•••••••• $cardNumber",
            style = TextStyle(
                color = colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                fontFamily = SoraFontFamily,
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(
            text = "VISA",
            style = TextStyle(
                color = colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = SoraFontFamily
            )
        )
    }
}