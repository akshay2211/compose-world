package compose.world

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeblur.BoxBlurExample
import compose.world.composables.animated_balance_increase.AnimatedBalanceIncreasePrev
import compose.world.composables.animated_navigation_drawer.HorizontalAnimatedNavigationDrawer
import compose.world.composables.card_swap.CardSwap
import compose.world.composables.circular_countdown.CircularCountdownPrev
import compose.world.composables.composable_tear.ComposableTearAnimation
import compose.world.composables.compose_ninja.ComposeNinja
import compose.world.composables.custom_lazy_columns.LazyColumnWithRefreshPrev
import compose.world.composables.customizable_chart.ChartUsage
import compose.world.composables.diagonal_transition.DiagonalTransition
import compose.world.composables.draggable_column.DraggableColumnPrev
import compose.world.composables.dynamic_graph.DynamicGraphPrev
import compose.world.composables.goofy_onboarding.GoofyOnboarding
import compose.world.composables.highlightable.InstagramHomePage
import compose.world.composables.horizontal_pager_dummy.DummyPagerTest
import compose.world.composables.knob_picker.KnobPickerUsageExample
import compose.world.composables.loading_animation.ShimmerTextPrev
import compose.world.composables.lock_screen.AnyLockPatternPrev
import compose.world.composables.lock_screen.DotLockPatternPrev
import compose.world.composables.motion.MotionLikeLayoutPrev
import compose.world.composables.motion_like_screen.MotionLikeScreen
import compose.world.composables.motion_search.MotionSearchPrev
import compose.world.composables.number_display.SendMoneyScreen
import compose.world.composables.number_picker.IosPickerPrev
import compose.world.composables.paging4.Paging4
import compose.world.composables.physics.CollisionDetection
import compose.world.composables.picker.IOSStylePickerPrev
import compose.world.composables.practice_3d.Practice3DPrev
import compose.world.composables.practice_3d.Practice3DPrevV2
import compose.world.composables.reorderable_column.ReorderableColumnPrev
import compose.world.composables.reorderable_lazt_list_remastered.ReorderableLazyListUsageExample
import compose.world.composables.rotating_cube.Rotating3DObjectUsageExample
import compose.world.composables.scaling_dots.ScalingDots
import compose.world.composables.shrink_layout.InstagramDemo
import compose.world.composables.video_editor.TimelineUsage
import compose.world.composables.reorderable_lazy_list_v3.ExampleUsage as ReorderableV3Usage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamplesListScreen() {
    var selectedExample by remember { mutableStateOf<ExampleItem?>(null) }

    val examples = remember {
        listOf(
            ExampleItem(
                title = "Customizable Chart",
                description = "Interactive customizable chart with animated progress and time selection"
            ) { ChartUsage() }, ExampleItem(
                title = "Draggable Column",
                description = "Reorderable column with drag-and-drop functionality"
            ) { DraggableColumnPrev() }, ExampleItem(
                title = "Dynamic Graph",
                description = "Interactive node graph with pan, zoom, and rotation"
            ) { DynamicGraphPrev() }, ExampleItem(
                title = "Reorderable Lazy List (Remastered)",
                description = "Remastered version of reorderable lazy list with smooth animations"
            ) { ReorderableLazyListUsageExample() }, ExampleItem(
                title = "Send Money Screen",
                description = "Animated number display with sliding digits and number pad"
            ) { SendMoneyScreen() }, ExampleItem(
                title = "Card Swap",
                description = "Swipeable card stack with depth and scaling effects"
            ) { CardSwap() }, ExampleItem(
                title = "Rotating 3D Object",
                description = "3D rotating objects with perspective rendering"
            ) { Rotating3DObjectUsageExample() }, ExampleItem(
                title = "iOS Style Picker", description = "iOS-style picker with 3D rotation effect"
            ) { IOSStylePickerPrev() }, ExampleItem(
                title = "Circular Timer",
                description = "Circular countdown timer with gradient arc progress"
            ) { CircularCountdownPrev() }, ExampleItem(
                title = "Instagram Home Page",
                description = "Instagram-like UI with highlightable elements"
            ) { InstagramHomePage() }, ExampleItem(
                title = "Knob Picker",
                description = "Circular knob picker for yes/no/unsure selection"
            ) { KnobPickerUsageExample() }, ExampleItem(
                title = "Number Picker",
                description = "Vertical number picker with 3D perspective effect"
            ) { IosPickerPrev() }, ExampleItem(
                title = "Video Editor Timeline",
                description = "Video editor timeline with multiple tracks"
            ) { TimelineUsage() }, ExampleItem(
                title = "Synchronized Pagers",
                description = "Horizontal pagers with bidirectional scroll sync"
            ) { DummyPagerTest() }, ExampleItem(
                title = "Motion Search", description = "Search screen with motion layout transition"
            ) { MotionSearchPrev() }, ExampleItem(
                title = "Box Blur", description = "Box blur shader effect applied to images"
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    BoxBlurExample()
                }
            }, ExampleItem(
                title = "Motion Like Layout",
                description = "Motion layout with three states and transitions"
            ) { MotionLikeLayoutPrev() }, ExampleItem(
                title = "Reorderable Column",
                description = "Column with drag-to-reorder functionality"
            ) { ReorderableColumnPrev() }, ExampleItem(
                title = "Reorderable Lazy List",
                description = "Lazy list with long-press drag-to-reorder"
            ) { ReorderableLazyListUsageExample() }, ExampleItem(
                title = "Shimmer Text", description = "Shimmer loading animation for text"
            ) { ShimmerTextPrev() }, ExampleItem(
                title = "Diagonal Transition",
                description = "Diagonal slide-in/out transitions between screens"
            ) { DiagonalTransition() }, ExampleItem(
                title = "Instagram Demo",
                description = "Instagram-style post interface with comments"
            ) { InstagramDemo {} }, ExampleItem(
                title = "Motion Like Screen",
                description = "Nested scroll with collapsible sections"
            ) { MotionLikeScreen() }, ExampleItem(
                title = "Animated Balance",
                description = "Wallet balance card with expandable payment modes"
            ) { AnimatedBalanceIncreasePrev() }, ExampleItem(
                title = "Pull to Refresh", description = "iOS-style pull-to-refresh for LazyColumn"
            ) { LazyColumnWithRefreshPrev() }, ExampleItem(
                title = "Compose Ninja",
                description = "Fruit Ninja-style game with composable slicing"
            ) { ComposeNinja() }, ExampleItem(
                title = "Composable Tear", description = "Shader-based tear/distortion effect"
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ComposableTearAnimation()
                }
            }, ExampleItem(
                title = "Reorderable List V3",
                description = "Third version with boundary constraints"
            ) { ReorderableV3Usage() }, ExampleItem(
                title = "Scaling Dots", description = "Interactive grid of dots that scale on touch"
            ) { ScalingDots() }, ExampleItem(
                title = "Physics Collision", description = "Physics-based collision detection"
            ) { CollisionDetection() }, ExampleItem(
                title = "Dot Lock Pattern", description = "Android-style pattern lock"
            ) { DotLockPatternPrev() }, ExampleItem(
                title = "Any Lock Pattern", description = "Android-style pattern lock"
            ) { AnyLockPatternPrev() }, ExampleItem(
                title = "3D Envelope", description = "3D envelope animation with perspective"
            ) { Practice3DPrev() }, ExampleItem(
                title = "3D Envelope v2", description = "3D envelope animation with perspective"
            ) { Practice3DPrevV2() }, ExampleItem(
                title = "Pagination", description = "Lazy list with pagination support"
            ) { Paging4() }, ExampleItem(
                title = "Animated Navigation Drawer",
                description = "Navigation drawer with 3D rotation effects"
            ) { HorizontalAnimatedNavigationDrawer() }, ExampleItem(
                title = "Goofy Onboarding", description = "Playful onboarding with rotating shapes"
            ) { GoofyOnboarding() })
    }

    if (selectedExample == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Compose World Examples", fontWeight = FontWeight.Bold
                        )
                    }, colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(examples) { example ->
                    ExampleCard(
                        example = example, onClick = { selectedExample = example })
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedExample!!.title) }, navigationIcon = {
                    IconButton(onClick = { selectedExample = null }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                )
            }) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                selectedExample!!.content()
            }
        }
    }
}

@Composable
fun ExampleCard(
    example: ExampleItem, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = example.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = example.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}