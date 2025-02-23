package compose.world.composables.diagonal_transition.screens

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.world.R
import java.time.format.TextStyle

@Composable
fun ScreenNone() {
    Column(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Are you ready?",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF90BEDE),
            style = androidx.compose.ui.text.TextStyle(
                drawStyle = Stroke(
                    width = 5f,
                    join = StrokeJoin.Round
                )
            )
        )
        Image(
            painter = painterResource(R.drawable.img_android),
            contentDescription = "android"
        )
    }
}