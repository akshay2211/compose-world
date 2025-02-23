package compose.world.composables.diagonal_transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import compose.world.composables.diagonal_transition.components.ScreenButtonLeft
import compose.world.composables.diagonal_transition.components.ScreenButtonRight
import compose.world.composables.diagonal_transition.screens.ScreenNone
import compose.world.composables.diagonal_transition.screens.ScreenOne
import compose.world.composables.diagonal_transition.screens.ScreenTwo

enum class DiagonalScreen(val color: Color) {
    NONE(color = Color.White),
    SCREEN_1(color = Color(0xFF5dd9c1)),
    SCREEN_2(color = Color.Red),
    SCREEN_3(color = Color.White)
}

@Composable
fun DiagonalTransition() {
    Box(modifier = Modifier.fillMaxSize()) {

        var visibleScreen by remember { mutableStateOf(DiagonalScreen.NONE) }

        // Screens
        DiagonalScreen.entries.forEach { screen ->
            AnimatedVisibility(
                modifier = Modifier.zIndex((screen.ordinal + 1F)),
                enter = slideInDiagonally(),
                exit = slideOutDiagonally(),
                visible = visibleScreen == screen
            ) {
                when (screen) {
                    DiagonalScreen.NONE -> ScreenNone()
                    DiagonalScreen.SCREEN_1 -> ScreenOne()
                    DiagonalScreen.SCREEN_2 -> ScreenTwo()
                    DiagonalScreen.SCREEN_3 -> Unit
                }
            }
        }

        // ---- Buttons
        // Start button
        var isStartButtonVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { isStartButtonVisible = true }
        ScreenButtonRight(
            modifier = Modifier.zIndex(1F),
            text = "Start",
            color = Color(0xFFF9A620),
            shouldExpand = visibleScreen == DiagonalScreen.SCREEN_1,
            buttonAppearAnimationDelay = 0,
            isButtonVisible = isStartButtonVisible,
            onClick = {
                visibleScreen = DiagonalScreen.SCREEN_1
            }
        )

        // Next button
        val isNextButtonVisible by remember(visibleScreen) {
            mutableStateOf(visibleScreen != DiagonalScreen.NONE)
        }
        ScreenButtonRight(
            modifier = Modifier.zIndex(2F),
            text = "Next",
            color = Color(0xFFD2D5DD),
            isButtonVisible = isNextButtonVisible,
            shouldExpand = visibleScreen == DiagonalScreen.SCREEN_2,
            buttonAppearAnimationDelay = 500,
            onClick = {
                visibleScreen = DiagonalScreen.SCREEN_2
            }
        )

        // Back button
        ScreenButtonLeft(
            modifier = Modifier.zIndex(2F),
            text = "Go back",
            color = Color.Red,
            isButtonVisible = visibleScreen == DiagonalScreen.SCREEN_1,
            shouldExpand = false,
            buttonAppearAnimationDelay = 750,
            onClick = {
                visibleScreen = DiagonalScreen.NONE
            }
        )


        // Back button 2
        val isGoBackButtonVisible by remember(visibleScreen) {
            mutableStateOf(visibleScreen == DiagonalScreen.SCREEN_2)
        }
        ScreenButtonRight(
            modifier = Modifier.zIndex(3F),
            text = "Go back",
            color = Color.Red,
            shouldExpand = visibleScreen == DiagonalScreen.SCREEN_3,
            buttonAppearAnimationDelay = 500,
            isButtonVisible = isGoBackButtonVisible,
            onClick = {
                visibleScreen = DiagonalScreen.SCREEN_1
            }
        )
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun DiagonalTransitionPrev() {
    DiagonalTransition()
}