package compose.world.composables.knob_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview(showBackground = true)
@Composable
fun KnobPickerUsageExample() {

    var progress by remember { mutableIntStateOf(value = 0) }

    var selection by remember { mutableStateOf(KnobChoice.UNSURE) }

    LaunchedEffect(progress) {
        if (progress <= 20 || progress >= 80) {
            selection = KnobChoice.UNSURE
        } else if (progress <= 50) {
            selection = KnobChoice.NO
        } else {
            selection = KnobChoice.YES
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        KnobChoiceText(
            choice = KnobChoice.UNSURE,
            selectedChoice = selection
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            KnobChoiceText(
                choice = KnobChoice.YES,
                selectedChoice = selection
            )
            SelectionKnob(
                circleRadius = 100.dp,
                viewPortWidth = 200.dp,
                viewPortHeight = 200.dp,
                onProgressChanged = { newProgress ->
                    progress = newProgress
                }
            )
            KnobChoiceText(
                choice = KnobChoice.NO,
                selectedChoice = selection
            )
        }
    }
}


@Composable
fun KnobChoiceText(
    choice: KnobChoice,
    selectedChoice: KnobChoice,
) {
    val selectionProgress by animateFloatAsState(
        targetValue = if (choice == selectedChoice) 1F else 0F
    )
    Text(
        modifier = Modifier.graphicsLayer {
            scaleX = 1F + selectionProgress / 2
            scaleY = 1F + selectionProgress / 2
        },
        text = choice.text,
        fontWeight = if (choice == selectedChoice) FontWeight.Bold else FontWeight.Normal,
        fontSize = 20.sp,
        color = if (choice == selectedChoice) choice.selectedColor else Color.Black
    )
}

enum class KnobChoice(val text: String, val selectedColor: Color) {
    YES(
        text = "YES :D",
        selectedColor = Color(0xFF00FF66)
    ),
    NO(
        text = "NO :\\",
        selectedColor = Color.Red
    ),
    UNSURE(
        text = "~ NOT SURE ~",
        selectedColor = Color(0xFFF9CB40)
    ),
    NONE (
        text = "",
        selectedColor = Color.White
    )
}