package compose.world.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R
import compose.world.ui.theme.PoppinsFontFamily


@Composable
fun SendMoneyScreen() {
    var price by remember { mutableLongStateOf(0L) }

    Column (
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black)
            .padding(horizontal = 16.dp, vertical = 48.dp)
    ) {
        Text(
            text = "Send Money",
            style = TextStyle(
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontFamily = PoppinsFontFamily
            )
        )
        Spacer(modifier = Modifier.height(56.dp))
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(120.dp),
                painter = painterResource(R.drawable.img_pfp_placeholder),
                contentDescription = "person",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "F. Justin",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    fontFamily = PoppinsFontFamily
                )
            )
            Spacer(modifier = Modifier.height(56.dp))
            Column {
                AnimatedNumberDisplay(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 50.dp),
                    number = price,
                    spacing = 12.dp,
                    textStyle = TextStyle(fontSize = 32.sp)
                )
                AnimatedVisibility(
                    visible = "$price".length == 10,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 50.dp),
                        text = "\uD83D\uDE14  Max length reached",
                        color = Color.Red
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        NumberPad(
            onDigitDeleted = {
                val stringPrice = price.toString()
                if (stringPrice.length == 1) {
                    price = 0L
                    return@NumberPad
                }
                price = stringPrice
                    .substring(
                        startIndex = 0,
                        endIndex = stringPrice.lastIndex
                    ).toLong()
            },
            onDigitEntered = {
                if ("$price".length >= 10) return@NumberPad

                price = "$price$it".toLong()
            }
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    color = Color.White
                ).padding(vertical = 10.dp),
            text = "Continue",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal,
                fontFamily = PoppinsFontFamily,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun AnimatedNumberDisplay(
    modifier: Modifier = Modifier,
    number: Long,
    spacing: Dp,
    textStyle: TextStyle
) {
    val textMeasurer = rememberTextMeasurer()
    val digits by remember(number) { mutableStateOf("$number") }
    val paddedDigits by remember(number) { mutableStateOf(digits.padEnd(length = 10, padChar = ' ')) }
    val digitWidth = remember { textMeasurer.measure(text = "1", style = textStyle).size.width }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "₼ ",
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        val animatedCommaCoordinate by animateFloatAsState(
            targetValue = (digits.length - 3) * digitWidth * 1F
        )
        Row(
            modifier = Modifier.drawBehind {
                val commaTextResult = textMeasurer.measure(
                    text = ",",
                    style = TextStyle(
                        fontSize = 32.sp,
                        color = Color.White
                    )
                )
                val commaCount = (digits.length - 1) / 3
                repeat(commaCount) { commaIndex ->
                    val xCoordinate =
                        animatedCommaCoordinate - (digitWidth * 3 * commaIndex) - (commaIndex + 1 - commaCount) * spacing.toPx() + (spacing.toPx() / 2F) - commaTextResult.size.width / 2F // - (spacing.toPx() / 2F)
                    drawText(
                        textLayoutResult = commaTextResult,
                        topLeft = Offset(
                            x = xCoordinate,
                            y = size.height - commaTextResult.size.height
                        )
                    )
                }
            }
        ) {
            paddedDigits.forEachIndexed { index, digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        slideInVertically { it } + fadeIn(
                            animationSpec = tween(750)
                        ) togetherWith
                                slideOutVertically { it } + fadeOut(
                            animationSpec = tween(750)
                        )
                    },
                    label = "animated_digit"
                ) { d ->
                    Text(
                        text = "$d",
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                val animatedWidth by animateDpAsState(
                    if (((digits.length - 4) - index) % 3 == 0) spacing
                    else 0.dp
                )
                Spacer(modifier = Modifier.width(animatedWidth))
            }
        }
    }
}

@Composable
fun NumberPad(
    onDigitEntered: (Int) -> Unit,
    onDigitDeleted: () -> Unit
) {
    Column {
        repeat(3) { row ->
            Row {
                repeat(3) { col ->
                    val digit = row * 3 + col + 1
                    Text(
                        modifier = Modifier
                            .weight(1F)
                            .clickable(
                                interactionSource = null,
                                indication = rememberRipple(bounded = false, radius = 32.dp, color = Color.White)
                            ) {
                                onDigitEntered(digit)
                            },
                        text = "$digit",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(56.dp))
        }
        Row {
            Text(
                modifier = Modifier.weight(1F),
                text = ""
            )
            Text(
                modifier = Modifier
                    .weight(1F)
                    .clickable(
                        interactionSource = null,
                        indication = rememberRipple(bounded = false, radius = 32.dp, color = Color.White)
                    ) {
                        onDigitEntered(0)
                    },
                text = "0",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                modifier = Modifier
                    .weight(1F)
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = onDigitDeleted
                    ),
                text = "Del",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AnimatedNumberPadPrev() {
    SendMoneyScreen()
}