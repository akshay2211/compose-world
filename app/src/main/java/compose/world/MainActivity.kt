package compose.world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import compose.world.composables.shrink_layout.ShrinkLayoutExample
import compose.world.composables.shrink_layout.InstagramDemo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box {
                var isVisible by remember { mutableStateOf(false) }
                InstagramDemo(
                    onCommentClicked = {
                        isVisible = true
                    }
                )
                ShrinkLayoutExample(
                    isVisible = isVisible,
                    onDismiss = {
                        isVisible = false
                    }
                )
            }
        }
    }
}