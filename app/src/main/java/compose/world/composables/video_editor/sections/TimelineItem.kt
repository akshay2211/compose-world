package compose.world.composables.video_editor.sections

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.util.fastAny

interface TimelineItem {
    val id: String
    val timelineStartMillis: Long
    val timelineEndMillis: Long
    val offsetMillis: Long
    val verticalLayerIndex: Int

    val lengthInMillis: Long

    val currentDurationInMillis get() = timelineEndMillis - timelineStartMillis

    fun copy(
        id: String = this.id,
        timelineStartMillis: Long = this.timelineStartMillis,
        timelineEndMillis: Long = this.timelineEndMillis,
        verticalLayerIndex: Int = this.verticalLayerIndex,
        offsetMillis: Long = this.offsetMillis,
    ): TimelineItem
}

val TimelineItem.type
    get() = when (this) {
        is TimelineText -> TimelineItemType.TEXT
        is TimelineVideo -> TimelineItemType.VIDEO
        is TimelineAudio -> TimelineItemType.AUDIO
        else -> error("Unknown TimelineItem $this")
    }

val TimelineItemType.klass
    get() = when (this) {
        TimelineItemType.TEXT -> TimelineText::class
        TimelineItemType.VIDEO -> TimelineVideo::class
        TimelineItemType.AUDIO -> TimelineAudio::class
    }

enum class TimelineItemType {
    TEXT, VIDEO, AUDIO
}

fun <T : TimelineItem> T.dragBy(durationInMillis: Long): T {
    val newStartMillis = (timelineStartMillis + durationInMillis).coerceAtLeast(0L)

    return copy(
        timelineStartMillis = newStartMillis,
        timelineEndMillis = (timelineEndMillis + (newStartMillis - timelineStartMillis))
    ) as T
}

fun <T : TimelineItem> T.adjustEndMillisBy(durationInMillis: Long): T {
    // Cannot exceed video length, and should keep under start millis.
    val newEndMillis = (timelineEndMillis + durationInMillis)
        .coerceIn(
            minimumValue = timelineStartMillis,
            maximumValue = ((timelineStartMillis + lengthInMillis) - offsetMillis)
        )
    return copy(
        timelineEndMillis = newEndMillis
    ) as T
}

fun <T : TimelineItem> T.setEndMillisTo(durationInMillis: Long): T {
    return copy(
        timelineEndMillis = durationInMillis.coerceAtLeast(minimumValue = timelineStartMillis)
    ) as T
}

fun <T : TimelineItem> T.setStartMillisTo(durationInMillis: Long): T {
    val newStartMillis = durationInMillis.coerceIn(
        minimumValue = timelineStartMillis - offsetMillis,
        maximumValue = timelineEndMillis
    )
    return copy(
        timelineStartMillis = newStartMillis,
        offsetMillis = offsetMillis + (newStartMillis - timelineStartMillis)
    ) as T
}


fun <T : TimelineItem> T.adjustStartMillisBy(durationInMillis: Long): T {
    val newStartMillis = (timelineStartMillis + durationInMillis).coerceIn(
        timelineStartMillis - offsetMillis,
        timelineEndMillis
    )
    return copy(
        timelineStartMillis = newStartMillis,
        offsetMillis = offsetMillis + (newStartMillis - timelineStartMillis)
    ) as T
}

fun <T : TimelineItem> T.adjustLayerBy(indexDelta: Int): T {
    return setLayerTo(verticalLayerIndex + indexDelta)
}

fun <T : TimelineItem> T.setLayerTo(layerIndex: Int): T {
    return copy(
        verticalLayerIndex = layerIndex.coerceAtLeast(0)
    ) as T
}

fun SnapshotStateList<TimelineItem>.checkIfItemOverlaps(
    item: TimelineItem
) : Boolean {
    val lengthSets = filter { it.verticalLayerIndex == item.verticalLayerIndex && (item.id != it.id) }
        .map { it.timelineStartMillis to it.timelineEndMillis }

    return lengthSets.fastAny {
        !((it.first < item.timelineStartMillis && it.second < item.timelineStartMillis) ||
                (it.first > item.timelineEndMillis && it.second > item.timelineEndMillis))
    }
}

fun SnapshotStateList<TimelineItem>.updateWithOverlapCheck(
    newItem : TimelineItem,
    index: Int
) {
    if (checkIfItemOverlaps(newItem)) return
    this[index] = newItem
}

fun SnapshotStateList<TimelineItem>.removeVerticalGaps() {
    // Step 1 - Get all distinct used vertical indices & max value
    val allVerticalLayerIndices = map { it.verticalLayerIndex }.toSet()
    val maxLayerIndex = allVerticalLayerIndices.max()

    // Step 2 - Find where gap starts and how wide. For ex: {1:4} entry means gap starts at index 1 and spans 4 layers
    var lastUnusedLayerIndex: Int? = null
    val unusedLayerIndicesWithGap = mutableMapOf<Int, Int>()
    for (i in (0..maxLayerIndex)) {
        if (i in allVerticalLayerIndices) continue

        if (lastUnusedLayerIndex == null) {
            lastUnusedLayerIndex = i
            unusedLayerIndicesWithGap[i] = 1
        } else {
            val lastUnusedLayerGap = unusedLayerIndicesWithGap[lastUnusedLayerIndex]!!
            val isContinuationOfExistingGap =
                (i - lastUnusedLayerIndex) - (lastUnusedLayerGap - 1) == 1
            if (!isContinuationOfExistingGap) {
                lastUnusedLayerIndex = i
                unusedLayerIndicesWithGap[i] = 1
                continue
            }

            unusedLayerIndicesWithGap[lastUnusedLayerIndex] = lastUnusedLayerGap + 1
        }
    }

    if (unusedLayerIndicesWithGap.isEmpty()) return

    // Step 3 - If there is some gap, determine how much each item should be shifted
    for (i in 0..lastIndex) {
        val item = this[i]
        var matchingIndex = 0
        for (j in unusedLayerIndicesWithGap.keys) {
            if (item.verticalLayerIndex < j) break
            matchingIndex++
        }
        if (matchingIndex == 0) continue
        val adjustBy = unusedLayerIndicesWithGap.values.take(matchingIndex).sum()
        this[i] = item.adjustLayerBy(-adjustBy)
    }
}