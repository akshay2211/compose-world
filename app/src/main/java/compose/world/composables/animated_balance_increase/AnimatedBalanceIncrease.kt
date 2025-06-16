package compose.world.composables.animated_balance_increase

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import compose.world.R
import compose.world.composables.animated_balance_increase.components.AddCashButton
import compose.world.composables.animated_balance_increase.components.AnimatedPaymentItemSelection
import compose.world.composables.animated_balance_increase.components.MinimizeButton
import compose.world.composables.animated_balance_increase.components.SizeCalculator
import compose.world.ui.theme.SoraFontFamily

class ScreenColorScheme(
    val primary: Color = Color.White,
    val onPrimary: Color = Color(0xFFBBBBBB),
    val primaryContainer: Color = Color(0xFFF5F5F5),
    val outline: Color = Color(0xFFEEEEEE),
    val secondary: Color = Color.Black,
    val onSecondary: Color = primary,
    val onSurface: Color = Color(0xFF555555),
)

val LocalColorScheme = compositionLocalOf { ScreenColorScheme() }

@Composable
private fun AnimatedBalanceIncrease(
    colorScheme: ScreenColorScheme,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1F else 0F,
        animationSpec = tween(durationMillis = 2000)
    )
    var bottomSectionTotalSize by remember { mutableStateOf(IntSize.Zero) }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8F)
            .clip(RoundedCornerShape(10.dp))
            .background(color = colorScheme.primary)
            .border(
                width = 1.dp, color = colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {

        TopSection(
            isExpanded = isExpanded,
            animationProgress = animationProgress,
            bottomSectionTotalSize = bottomSectionTotalSize,
            onAddCash = {
                if (!isExpanded) isExpanded = true
                else if (animationProgress != 1F) Unit
                else { /* Code for actually adding cash */ }
            },
            onMinimize = { isExpanded = false }
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = animationProgress }
        ) {
            drawLine(
                color = colorScheme.outline,
                start = Offset(-100F, size.height),
                end = Offset(size.width + 100F, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }

        BottomSection(
            animationProgress = animationProgress,
            onSizeChanged = { bottomSectionTotalSize = it }
        )
    }
}

@Composable
private fun BottomSection(
    animationProgress: Float,
    onSizeChanged: (IntSize) -> Unit,
) {
    val density = LocalDensity.current
    val colorScheme = LocalColorScheme.current

    SizeCalculator(
        modifiersAfterSizeCalculation = { calculatedSize ->
            Modifier
                .heightIn(max = density.run { calculatedSize.height.toDp() } * animationProgress)
                .verticalScroll(rememberScrollState())
        },
        onSizeChanged = onSizeChanged,
        content = {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Payment mode",
                    style = TextStyle(
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        fontFamily = SoraFontFamily
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                var selectedCardIndex by remember { mutableIntStateOf(0) }
                AnimatedPaymentItemSelection(
                    cards = listOf("5665", "4927", "1335"),
                    selectedCardIndex = selectedCardIndex,
                    onCardSelected = { selectedCardIndex = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AddCashButton(
                    modifier = Modifier.graphicsLayer {
                        scaleX = 0F
                        scaleY = 0F
                    },
                    onButtonClick = {}
                )
            }
        }
    )
}

@Composable
private fun TopSection(
    animationProgress: Float,
    isExpanded: Boolean,
    bottomSectionTotalSize: IntSize,
    onAddCash: () -> Unit,
    onMinimize: () -> Unit,
) {
    val colorScheme = LocalColorScheme.current
    var topSectionTotalSize by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = Modifier
            .zIndex(100F)
            .onSizeChanged {
                topSectionTotalSize = it
            }
    ) {
        // Wallet balance section
        Row(
            modifier = Modifier
                .graphicsLayer {
                    val scaleDecrease = animationProgress * 0.2F
                    scaleX = 1F - scaleDecrease
                    scaleY = 1F - scaleDecrease
                    translationX = -(size.width * scaleDecrease / 2)
                    translationY = -(size.height * scaleDecrease / 2)
                }
        ) {
            Image(
                modifier = Modifier
                    .background(
                        color = colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
                    .size(16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_wallet),
                colorFilter = ColorFilter.tint(color = colorScheme.secondary),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Wallet",
                    style = TextStyle(
                        color = colorScheme.onPrimary,
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = "$34.00",
                    style = TextStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = SoraFontFamily
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1F))

        // Minimize button & Add cash button section
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
        ) {
            if (isExpanded) {
                MinimizeButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .graphicsLayer {
                            alpha = animationProgress
                        },
                    onClick = onMinimize
                )
            }
            AddCashButton(
                modifier = Modifier
                    .graphicsLayer {
                        val distanceToBottomSectionY = (bottomSectionTotalSize.height + topSectionTotalSize.height) - size.height
                        val distanceToBottomSectionX = -(bottomSectionTotalSize.width.toFloat() - size.width)
                        translationY = distanceToBottomSectionY * animationProgress
                        translationX = distanceToBottomSectionX * animationProgress
                    },
                onButtonClick = onAddCash
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AnimatedBalanceIncreasePrev() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBalanceIncrease(
            colorScheme = ScreenColorScheme()
        )
    }
}