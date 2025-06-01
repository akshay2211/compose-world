package compose.world.composables.animated_navigation_drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DummyHorizontalNavigationDrawer(
    screenHeight: Dp,
    animateToScale: Float,
    progress: Float
) {
    Column (
        modifier = Modifier
            .padding(start = 12.dp)
            .padding(top = (screenHeight * (1F - animateToScale) / 2) * progress),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Icons.Default.Home to "Home",
            Icons.Default.Settings to "Settings",
            Icons.Default.Favorite to "Favorites",
            Icons.Default.Person to "Profile"
        ).forEachIndexed { index, pair ->
            Row (
                modifier = Modifier.graphicsLayer {
                    val modifiedAnimationProgressForReveal = (progress * 4 - index).coerceIn(0F, 1F)
                    val widthWithPadding = size.width + 12.dp.toPx()

                    translationX = -widthWithPadding + modifiedAnimationProgressForReveal * widthWithPadding
                    alpha = modifiedAnimationProgressForReveal
                },
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = pair.first,
                    contentDescription = "home",
                    tint = Color.Black
                )
                Text(
                    text = pair.second,
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}