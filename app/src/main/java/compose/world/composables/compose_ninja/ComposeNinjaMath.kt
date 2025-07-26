package compose.world.composables.compose_ninja

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.abs


// Warning - AI generated functions - Probably not very optimized, but gets the job done

fun findCutPoints(cuts: List<Pair<Offset, Offset>>, fruits: List<Fruit>): Map<String, Pair<Offset, Offset>> {
    val result = mutableMapOf<String, Pair<Offset, Offset>>()

    fruits.forEach { fruit ->
        val fruitRect = Rect(
            offset = Offset(fruit.translationX, fruit.translationY),
            size = fruit.size
        )

        val intersectionPoints = mutableListOf<Offset>()

        // Check all cut lines against this fruit
        cuts.forEach { (start, end) ->
            val intersections = findLineRectIntersections(start, end, fruitRect)
            intersectionPoints.addAll(intersections)
        }

        // If we have at least 2 intersection points, find entry and exit
        if (intersectionPoints.size >= 2) {
            // Sort points by distance from the first cut line's start point
            // This helps us identify the entry and exit points correctly
            val firstCutStart = cuts.firstOrNull()?.first ?: Offset.Zero
            val sortedPoints = intersectionPoints.sortedBy { point ->
                distanceSquared(firstCutStart, point)
            }

            // Take the first and last intersection points as entry and exit
            val entryPoint = sortedPoints.first()
            val exitPoint = sortedPoints.last()

            val sortedCutPoint = Pair(entryPoint, exitPoint).sortedVertically()
            val fruitTranslation = fruit.getTranslation()
            result[fruit.label] = sortedCutPoint.copy(
                first = sortedCutPoint.first - fruitTranslation,
                second = sortedCutPoint.second - fruitTranslation
            )
        }
    }

    return result
}

private fun findLineRectIntersections(lineStart: Offset, lineEnd: Offset, rect: Rect): List<Offset> {
    val intersections = mutableListOf<Offset>()

    // Check intersection with each edge of the rectangle
    val edges = listOf(
        // Top edge
        Pair(Offset(rect.left, rect.top), Offset(rect.right, rect.top)),
        // Right edge
        Pair(Offset(rect.right, rect.top), Offset(rect.right, rect.bottom)),
        // Bottom edge
        Pair(Offset(rect.right, rect.bottom), Offset(rect.left, rect.bottom)),
        // Left edge
        Pair(Offset(rect.left, rect.bottom), Offset(rect.left, rect.top))
    )

    edges.forEach { (edgeStart, edgeEnd) ->
        val intersection = findLineIntersection(lineStart, lineEnd, edgeStart, edgeEnd)
        if (intersection != null) {
            intersections.add(intersection)
        }
    }

    return intersections.distinct() // Remove any duplicate points
}

private fun findLineIntersection(
    line1Start: Offset, line1End: Offset,
    line2Start: Offset, line2End: Offset
): Offset? {
    val x1 = line1Start.x
    val y1 = line1Start.y
    val x2 = line1End.x
    val y2 = line1End.y
    val x3 = line2Start.x
    val y3 = line2Start.y
    val x4 = line2End.x
    val y4 = line2End.y

    val denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)

    if (abs(denominator) < 1e-10) {
        // Lines are parallel
        return null
    }

    val t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator
    val u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator

    // Check if intersection point lies within both line segments
    if ((t in 0.0..1.0) && (u in 0.0..1.0)) {
        val intersectionX = x1 + t * (x2 - x1)
        val intersectionY = y1 + t * (y2 - y1)
        return Offset(intersectionX, intersectionY)
    }

    return null
}

private fun distanceSquared(point1: Offset, point2: Offset): Float {
    val dx = point1.x - point2.x
    val dy = point1.y - point2.y
    return dx * dx + dy * dy
}

fun Pair<Offset, Offset>.sortedVertically() : Pair<Offset, Offset> {
    if (this.first.y <= this.second.y) return this
    else return this.second to this.first
}