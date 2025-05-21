package compose.world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import compose.world.composables.lock_screen.AnyLockPattern
import compose.world.composables.lock_screen.DotLockPattern
import compose.world.composables.lock_screen.LineParams
import compose.world.composables.rotating_cube.Rotating3DObjectUsageExample


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                DotLockPattern(
                    rowCount = 3,
                    columnCount = 3,
                    dotSize = 16.dp,
                    horizontalPadding = 88.dp,
                    columnPadding = 64.dp,
                    verticalPadding = 24.dp,
                    selectedDotColor = Color.Green,
                    selectionErrorRadius = 16.dp
                )

                HorizontalDivider()

                AnyLockPattern(
                    rowCount = 3,
                    columnCount = 3,
                    symbolSize = 32.dp,
                    horizontalPadding = 88.dp,
                    columnPadding = 64.dp,
                    verticalPadding = 24.dp,
                    selectionErrorRadius = 16.dp,
                    customDotContent = { isSelected ->
                        Text(
                            text = if (isSelected) "YES!" else "NO :/",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    lineParams = LineParams(
                        strokeWidth = 2.dp
                    )
                )
            }
        }
    }
}