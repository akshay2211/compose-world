package com.example.composeblur

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.world.R
import org.intellij.lang.annotations.Language

// AGSL shader code for box blur
@Language("AGSL")
private const val BOX_BLUR_SHADER = """
    uniform shader inputImage;
    uniform float blurRadius;
    uniform vec2 resolution;

    vec4 main(vec2 fragCoord) {
        vec2 uv = fragCoord / resolution;
        vec4 color = vec4(0.0);
        float totalWeight = 0.0;
        
        // Fixed kernel size (5x5 for example, adjustable via blurRadius scaling)
        const float kernelHalfSize = 2.0; // 5x5 kernel = 2 pixels in each direction
        
        // Scale the sampling step based on blurRadius
        float stepSize = blurRadius / kernelHalfSize;
        
        // Sample pixels in a square kernel
        for (float x = -kernelHalfSize; x <= kernelHalfSize; x += 1.0) {
            for (float y = -kernelHalfSize; y <= kernelHalfSize; y += 1.0) {
                vec2 offset = vec2(x, y) * stepSize / resolution;
                color += inputImage.eval(fragCoord + offset);
                totalWeight += 1.0;
            }
        }
        
        // Average the sampled colors
        color /= totalWeight;
        return color;
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BoxBlurComposable(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    val runtimeShader = remember { RuntimeShader(BOX_BLUR_SHADER) }

    Box(
        modifier = modifier
            .graphicsLayer {
                val shaderBrush = ShaderBrush(runtimeShader)
                runtimeShader.setFloatUniform("blurRadius", blurRadius.toPx() / 2f)
                runtimeShader.setFloatUniform("resolution", size.width, size.height)


                renderEffect = android.graphics.RenderEffect.createShaderEffect(
                    shaderBrush.createShader(size)
                ).asComposeRenderEffect()
            }
    ) {
        content()
    }
}

// Example usage
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
@Preview
fun BoxBlurExample() {
    BoxBlurComposable(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        blurRadius = 1000.dp
    ) {
        // Your content here, e.g., an Image or other Composables
        Image(
            painter = painterResource(id = R.drawable.img_vini),
            contentDescription = "Blurred Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}