package compose.world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import compose.world.composables.animated_balance_increase.AnimatedBalanceIncreasePrev
import compose.world.composables.animated_navigation_drawer.HorizontalAnimatedNavigationDrawer
import compose.world.composables.animated_navigation_drawer.VerticalAnimatedNavigationDrawer
import compose.world.composables.knob_picker.KnobPickerUsageExample
import compose.world.composables.picker.IOSStylePickerPrev


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            AnimatedBalanceIncreasePrev()
        }
    }
}