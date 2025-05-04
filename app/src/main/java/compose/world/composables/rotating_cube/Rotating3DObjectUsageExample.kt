package compose.world.composables.rotating_cube

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Triangle vertices (centered at origin, size 1) (x, y, z)
val triangleVertices = arrayOf(
    Vertex3D(x = -0.5F, y = -0.5F, z = -0.5F), // 0
    Vertex3D(x = 0.5F, y = -0.5F, z = -0.5F),  // 1
    Vertex3D(x = 0F, y = 0.5F, z = 0F),     // 2
    Vertex3D(x = -0.5F, y = -0.5F, z = 0.5F),  // 3
    Vertex3D(x = 0.5F, y = -0.5F, z = 0.5F),   // 4
    Vertex3D(x = 0F, y = 0.5F, z = 0.5F),      // 5
)

// Triangle faces (indices of vertices, 4 per face)
val triangleFaces = arrayOf(
    ObjectFace(color = Color.Red, vertexIndices = listOf(0, 1, 2)),
    ObjectFace(color = Color.Blue, vertexIndices = listOf(3, 4, 2)),
    ObjectFace(color = Color.Green, vertexIndices = listOf(1, 2, 4)),
    ObjectFace(color = Color.Yellow, vertexIndices = listOf(0, 2, 3)),
    ObjectFace(color = Color.Cyan, vertexIndices = listOf(0, 1, 4, 3))
)

// Cube vertices (centered at origin, size 1) (x, y, z)
val cubeVertices = arrayOf(
    Vertex3D(x = -0.5F, y = -0.5F, z = -0.5F), // 0
    Vertex3D(x = 0.5F, y = -0.5F, z = -0.5F),  // 1
    Vertex3D(x = 0.5F, y = 0.5F, z = -0.5F),   // 2
    Vertex3D(x = -0.5F, y = 0.5F, z = -0.5F),  // 3
    Vertex3D(x = -0.5F, y = -0.5F, z = 0.5F),  // 4
    Vertex3D(x = 0.5F, y = -0.5F, z = 0.5F),   // 5
    Vertex3D(x = 0.5F, y = 0.5F, z = 0.5F),    // 6
    Vertex3D(x = -0.5F, y = 0.5F, z = 0.5F)    // 7
)

// Cube faces (indices of vertices, 4 per face)
val cubeFaces = arrayOf(
    ObjectFace(color = Color.Red, vertexIndices = listOf(0, 1, 2, 3)),
    ObjectFace(color = Color.Blue, vertexIndices = listOf(5, 4, 7, 6)),
    ObjectFace(color = Color.Green, vertexIndices = listOf(1, 5, 6, 2)),
    ObjectFace(color = Color.Yellow, vertexIndices = listOf(4, 0, 3, 7)),
    ObjectFace(color = Color.Cyan, vertexIndices = listOf(3, 2, 6, 7)),
    ObjectFace(color = Color.Magenta, vertexIndices = listOf(0, 4, 5, 1))
)

enum class Shape {
    CUBE, TRIANGLE
}

@Preview
@Composable
fun Rotating3DObjectUsageExample() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        var distanceFromCamera by rememberSaveable { mutableFloatStateOf(2F) }
        var scale by rememberSaveable { mutableFloatStateOf(0.33F) }

        var selectedShape by remember { mutableStateOf(Shape.CUBE) }


        // Rotation angles (in radians)
        var rotationX by remember { mutableFloatStateOf(0f) }
        var rotationY by remember { mutableFloatStateOf(0f) }
        AnimatedContent(selectedShape) {
            when (it) {
                Shape.CUBE -> {
                    RotatingObject3D(
                        faces = cubeFaces,
                        vertices = cubeVertices,
                        perspectiveDistance = distanceFromCamera,
                        scaleFactor = scale,
                        rotationX = rotationX,
                        rotationY = rotationY,
                        onRotationChanged = { x, y->
                            rotationX += x
                            rotationY += y
                        }
                    )
                }
                Shape.TRIANGLE -> {
                    RotatingObject3D(
                        faces = triangleFaces,
                        vertices = triangleVertices,
                        perspectiveDistance = distanceFromCamera,
                        scaleFactor = scale,
                        rotationX = rotationX,
                        rotationY = rotationY,
                        onRotationChanged = { x, y->
                            rotationX += x
                            rotationY += y
                        }
                    )
                }
            }
        }


        // CONTROLS
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(text = "FOV (Field of view)", color = Color.White)
            Slider(
                value = distanceFromCamera,
                onValueChange = { distanceFromCamera = it },
                valueRange = 1F..10F
            )

            Text(text = "Distance from camera", color = Color.White)
            Slider(
                value = scale,
                onValueChange = { scale = it },
                valueRange = 0F..1F
            )


            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        selectedShape = when (selectedShape) {
                            Shape.CUBE -> Shape.TRIANGLE
                            Shape.TRIANGLE -> Shape.CUBE
                        }
                    }
                ) {
                    Text(text = "Change shape", color = Color.White)
                }

                val scope = rememberCoroutineScope()
                var autoRotateJob by remember { mutableStateOf<Job?>(null) }

                Button(
                    onClick = {
                        if (autoRotateJob != null) {
                            autoRotateJob?.cancel()
                            autoRotateJob = null
                        } else {
                            autoRotateJob = scope.launch {
                                while (true) {
                                    rotationX -= 0.002F
                                    rotationY += 0.01F
                                    delay(10)
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (autoRotateJob == null) "Auto rotate" else "Stop auto rotation",
                        color = Color.White
                    )
                }
            }
        }
    }
}