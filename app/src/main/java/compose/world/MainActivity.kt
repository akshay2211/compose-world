package compose.world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import compose.world.composables.custom_lazy_columns.LazyColumnWithRefresh
import compose.world.composables.custom_lazy_columns.LazyColumnWithRefreshPrev
import compose.world.composables.shrink_layout.ShrinkLayoutExample
import compose.world.composables.shrink_layout.InstagramDemo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box (
                modifier = Modifier
                    .background(color = Color(0xFF212529))
                    .padding(top = 32.dp)
            ) {
                LazyColumnWithRefreshPrev()
            }
        }
    }
}