package compose.world.composables.physics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.pow

sealed interface MovableObject {
    fun move(vector: Vector)
    fun collides(other: MovableObject): Boolean

    class Circle(
        val center: Vector,
        val radius: Float,
    ) : MovableObject {
        override fun move(vector: Vector) {
            center.modify(vector)
        }

        override fun collides(other: MovableObject): Boolean {
            when (other) {
                is Circle -> {
                    val xDist = abs(other.center.x - center.x)
                    val yDist = abs(other.center.y - center.y)
                    val centerDistSquared = yDist * yDist + xDist * xDist

                    return centerDistSquared <= (other.radius + radius).pow(2)
                }
            }
        }
    }
}

class MovableGameObject(
    val gameObject: MovableObject,
    val speedVector: Vector,
)

class Vector(x: Float, y: Float) {
    var x by mutableFloatStateOf(x)
    var y by mutableFloatStateOf(y)

    fun modify(other: Vector) {
        x += other.x
        y += other.y
    }

    fun invert() {
        x *= -1
        y *= -1
    }

    fun change(other: Vector): Vector {
        return Vector(x + other.x, y + other.y)
    }
}

val circles = listOf(
    MovableObject.Circle(
        center = Vector(120F, 100F),
        radius = 50F
    ),
    MovableObject.Circle(
        center = Vector(200F, 250F),
        radius = 50F
    )
)

val objects = listOf(
    MovableGameObject(
        gameObject = circles[0],
        speedVector = Vector(5F, 10F)
    ),
    MovableGameObject(
        gameObject = circles[1],
        speedVector = Vector(5F, 7F)
    )
)

@Composable
fun CollisionDetection() {

    // Velocity increaser
    LaunchedEffect(Unit) {
        while (true) {
            objects.forEach {
                it.gameObject.move(it.speedVector)
            }
            delay(10)
        }
    }
    // Collision detector
    LaunchedEffect(Unit) {
        while (true) {
            objects.forEach { first ->
                objects.forEach { second ->
                    if (first == second) return@forEach

                    if (first.gameObject.collides(second.gameObject)) {
                        println("COLLISION RESOLUTION REQUIRED!")
//                        first.speedVector.invert()
                    }
                }
            }
            delay(10)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        objects.forEach { movableObj ->
            when (val gameObj = movableObj.gameObject) {
                is MovableObject.Circle -> {
                    drawCircle(
                        color = Color.Black,
                        radius = gameObj.radius,
                        center = Offset(gameObj.center.x, gameObj.center.y)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollisionDetectionPrev() {
    CollisionDetection()
}