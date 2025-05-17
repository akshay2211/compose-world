package compose.world.composables.composable_tear

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val shaderCode = """ 
        uniform float2 resolution;
        uniform float time;
        uniform float progress;
        uniform shader inputShader;
        
        
        half4 main(float2 fragCoord) {
            
            float2 uv = fragCoord / resolution;
            float xCoordFloored = floor(fragCoord.x);
            float yCoordFloored = floor(fragCoord.y);
            float xCoord = xCoordFloored - mod(xCoordFloored, 45.0);
            float yCoord = yCoordFloored - mod(yCoordFloored, 45.0);
            
            bool isXEven = mod(xCoord, 2.0) == 0.0;
            bool isYEven = mod(yCoord, 2.0) == 0.0;
            
            if (isXEven && isYEven) {
                if (inputShader.eval(fragCoord != half4(0.0, 0.0, 0.0, 1.0)) {
                return half4(0.0, 1.0, 1.0, 1.0);
                }
                
            }
            
//            if (uv.y < 0.49) { 
//                return half4(1.0, 0, 0, 1.0);
//            }
//            
//            if (uv.y < 0.51) {
////                if (uv.x < 0.5) {
//                    return half4(0.0, 0, 0, 0.0);
////                }
//            }
            
            return inputShader.eval(fragCoord);
        }
    """.trim()

@RequiresApi(Build.VERSION_CODES.TIRAMISU) // Android 13+
@Composable
fun ComposableTearAnimation() {
    val context = LocalContext.current

    val shader = remember { RuntimeShader(shaderCode) }
    val infiniteAnim by rememberInfiniteTransition()
        .animateFloat(0F, 1F, infiniteRepeatable(tween(2000)))
    Box(
        modifier = Modifier
            .size(300.dp)
            .graphicsLayer {
                shader.setFloatUniform("time", infiniteAnim)
                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(shader, "inputShader")
                    .apply {
                        shader.setFloatUniform("resolution", size.width, size.height)
                    }
                    .asComposeRenderEffect()
            }
    ) {
        // 🌈 Everything inside here will be affected by the shader!
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Shader Time!", fontSize = 64.sp, color = Color.Black)
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Red)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
private fun ComposableTearAnimationPrev() {
    ComposableTearAnimation()
}