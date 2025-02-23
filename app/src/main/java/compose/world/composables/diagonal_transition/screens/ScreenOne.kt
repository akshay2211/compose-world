package compose.world.composables.diagonal_transition.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R

@Composable
fun ScreenOne() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF9A620))
            .padding(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1F),
                text = "JETPACK COMPOSE!",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            val infiniteRotation by rememberInfiniteTransition(
                label = "infinite"
            )
                .animateFloat(
                    initialValue = 0F,
                    targetValue = 360F,
                    animationSpec = infiniteRepeatable(
                        tween(
                            durationMillis = 2000,
                            easing = LinearEasing
                        )
                    ),
                    label = "logo_rotation"
                )
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        rotationZ = infiniteRotation
                    },
                painter = painterResource(R.drawable.logo_compose),
                contentDescription = "compose"
            )
        }

    }
}