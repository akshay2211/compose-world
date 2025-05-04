package compose.world.composables.rotating_cube

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.sin

/**
 * Author: Farid Guliyev
 * Date: [May 3, 2025]
 *
 * A Jetpack Compose composable that renders a 3D object with filled faces using perspective
 * projection. Supports interactive rotation via drag gestures and customizable geometry.
 *
 * @param vertices Array of 3D vertices (x, y, z) defining the object's geometry.
 * @param faces Array of faces, each with a label, color, and vertex indices forming a polygon.
 * @param perspectiveDistance Camera distance to projection plane, controlling FOV (default: 2f).
 * @param scaleFactor Scales the object relative to the Canvas size (default: 0.33f for 33%).
 * @param rotationX Rotation angle around X-axis (radians).
 * @param rotationY Rotation angle around Y-axis (radians).
 * @param onRotationChanged Callback for drag-based rotation updates (x, y deltas in radians).
 * @param unRenderedFaceLabels Labels of faces to exclude from rendering (default: empty).
 *
 * For more, you can visit: https://github.com/faridGuliyew/compose-world
 */


data class Vertex3D(
    val x: Float,
    val y: Float,
    val z: Float,
)

class ObjectFace(
    val label: String = "face",
    val color: Color = Color.White,
    val vertexIndices: List<Int>,
)

@Composable
fun RotatingObject3D(
    vertices: Array<Vertex3D>,
    faces: Array<ObjectFace>,
    perspectiveDistance: Float = 2F,
    scaleFactor: Float = 0.33F,
    rotationX: Float,
    rotationY: Float,
    onRotationChanged: (x: Float, y: Float) -> Unit,
    unRenderedFaceLabels: Set<String> = emptySet(),
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    onRotationChanged(-dragAmount.y * 0.01f, dragAmount.x * 0.01f)
                }
            }

    ) {
        // Rotate vertices using `Rotation matrix` technique
        val rotatedVertices = vertices.map { vertex ->
            val rotatedY = vertex.y * cos(rotationX) - vertex.z * sin(rotationX)
            val rotatedZ = vertex.y * sin(rotationX) + vertex.z * cos(rotationX)
            val rotatedX = vertex.x * cos(rotationY) + rotatedZ * sin(rotationY)

            val doubleRotatedZ = -vertex.x * sin(rotationY) + rotatedZ * cos(rotationY)
            vertex.copy(x = rotatedX, y = rotatedY, z = doubleRotatedZ)
        }

        // Project 3D to 2D using `perspective projection` technique
        val scale = size.minDimension * scaleFactor
        val vertices2D = rotatedVertices.map { vertex ->
            val x = vertex.x
            val y = vertex.y
            val factor = perspectiveDistance / (vertex.z + perspectiveDistance)
            Offset(
                x = (x * factor * scale) + size.width / 2,
                y = (y * factor * scale) + size.height / 2
            )
        }

        // Calculate face depths using (average Z of vertices for sorting). Sort back-to-front
        val sortedFacesByDepth = faces
            .sortedByDescending {
                it.vertexIndices.map { rotatedVertices[it].z }.average().toFloat()
            }


        val facePaths = buildList {
            // Connect edges for each face
            sortedFacesByDepth.forEach { face ->
                val faceIndices = face.vertexIndices
                val vertexCount = faceIndices.size

                val path = Path()
                val startOffset = vertices2D[faceIndices[0]]
                path.moveTo(startOffset.x, startOffset.y)

                for (i in 0 until vertexCount) {
                    val endOffset = vertices2D[faceIndices[(i + 1) % vertexCount]]
                    path.lineTo(endOffset.x, endOffset.y)
                }

                add(face to path)
            }
        }

        facePaths.forEachIndexed { index, (face, path) ->
            if (face.label in unRenderedFaceLabels) return@forEachIndexed

            drawPath(
                path = path,
                color = face.color,
                style = Fill
            )
        }
    }
}