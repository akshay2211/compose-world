package compose.world.composables.card_swap

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.abs

@Composable
fun CardSwap() {
    /* Progress: Stars with 0F, and keeps increasing by 1F after each card swipe */
    var progress by remember { mutableStateOf(0F) }
    val animatedProgress by animateFloatAsState(progress, animationSpec = spring())
    /* FrontCardIndex: As its name suggests, it is the index of the front card */
    var frontCardIndex by remember { mutableStateOf(0) }
    /* Similar to front index, but it is the index of the dragged card */
    var focusedCardIndex by remember { mutableStateOf(0) }
    // Width of the card & screen, necessary for following calculations.
    val cardWidth = 300.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        /* Add as many card as you wish */
        val cards = remember {
            listOf(
                SwappableCard(color = Color(0xFF0055AA)),
                SwappableCard(color = Color(0xFFAA5555)),
                SwappableCard(color = Color(0xFF22BB22),),
                SwappableCard(color = Color.Red),
                SwappableCard(color = Color(0xff4c1293)),
                SwappableCard(color = Color(0xffa9c81c)),
                SwappableCard(color = Color(0xffb6510a)),
                SwappableCard(color = Color(0xffb55274)),
            )
        }


        cards.forEachIndexed { index, card ->
            val animatedDrag by animateFloatAsState(card.dragAmountX, spring())
            /* Depth: Shows how further away the card is from dragged card */
            val depth = index - focusedCardIndex
            /* Alpha value, which is used to show cards in the back more transparent */
            val animatedAlpha by animateFloatAsState(targetValue = when {
                depth == 2 -> 0.3F
                depth >= 3 -> 0F
                else -> 1F
            }, animationSpec = tween(500))

            DemoCard(
                modifier = Modifier
                    .zIndex(100F - depth)
                    .graphicsLayer {
                        val defaultScale = 1F - (0.1F * index)
                        /* Card should not scale after swapped, therefore if check */
                        val isSwapped = index < focusedCardIndex
                        val scaleIncrement =
                            if (isSwapped) 0.1F * index
                            else (0.1F) * animatedProgress
                        val scale = defaultScale + scaleIncrement
                        val scaleDownBy = 1F - scale
                        scaleY = scale
                        scaleX = scale

                        val extraTranslationByScale = (180 * scaleDownBy).dp.toPx() / 2F
                        val maxVerticalTranslation = 8.dp * index - (8.dp * animatedProgress)
                        translationY = -(maxVerticalTranslation.toPx() + extraTranslationByScale)
                        translationX = animatedDrag

                        alpha = animatedAlpha
                    }
                    .pointerInput(Unit) {
                        detectDragGestures (
                            onDragEnd = {
                                val focusedCard = cards.getOrNull(focusedCardIndex) ?: return@detectDragGestures
                                val isPullingPrevCard = focusedCardIndex != index

                                val threshold = if (isPullingPrevCard) 0.9F else 0.2F

                                if (abs(focusedCard.dragAmountX) / cardWidth.toPx() > threshold) {
                                    if (focusedCard.dragAmountX > 0F) {
                                        frontCardIndex = focusedCardIndex - 1
                                        focusedCard.dragAmountX = ((screenWidth - cardWidth) / 2 + cardWidth).toPx()
                                    } else {
                                        frontCardIndex = focusedCardIndex + 1
                                        focusedCard.dragAmountX = -((screenWidth - cardWidth) / 2 + cardWidth).toPx()
                                    }
                                    progress = frontCardIndex.toFloat()
                                    focusedCardIndex = frontCardIndex
                                } else {
                                    focusedCard.dragAmountX = 0F
                                    progress = focusedCardIndex.toFloat()
                                }
                            }
                        ) { _, dragAmount ->
                            val focusedCard = cards.getOrNull(focusedCardIndex) ?: return@detectDragGestures
                            val isPullingPrevCard = focusedCardIndex != index

                            if (dragAmount.x < 0F) {
                                if (index == cards.lastIndex) return@detectDragGestures

                                focusedCard.dragAmountX += dragAmount.x
                            } else {
                                val shouldFocusToPrevCard = focusedCard.dragAmountX >= 0F

                                if (shouldFocusToPrevCard) {
                                    cards.getOrNull(index - 1)?.let { prevCard->
                                        prevCard.dragAmountX += dragAmount.x
                                        focusedCardIndex = index - 1
                                    }
                                } else {
                                    focusedCard.dragAmountX += dragAmount.x
                                }
                            }

                            if (isPullingPrevCard) {
                                progress = -focusedCard.dragAmountX / cardWidth.toPx() + (index - 1)
                            } else {
                                progress = -focusedCard.dragAmountX / cardWidth.toPx() + index
                            }
                        }
                    },
                width = cardWidth,
                color = card.color
            )
        }
    }
}

/* Any other detail can be added there */
class SwappableCard (
    val color: Color
) {
    var dragAmountX by mutableStateOf(0F)
}

@Composable
private fun DemoCard(
    modifier: Modifier = Modifier,
    width: Dp,
    color: Color,
) {
    Column (
        modifier = modifier
            .width(width)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = color)
            .padding(12.dp)
    ) {
        Text(
            text = "5167 5133 4070 8093",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "John Doe",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Light),
            color = Color.White,
        )
    }
}

@Preview (showBackground = true)
@Composable
private fun CardSwapPrev() {
    CardSwap()
}