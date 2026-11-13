package com.nuvio.app.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * How far a screen's large title has folded away. Scrolling up folds it before the list moves; scrolling down
 * unfolds it once the list is back at its top, the same as a large navigation title on iOS.
 *
 * Put [connection] on a parent of the scrolling list with `Modifier.nestedScroll`, and [collapsingTitle] on the title.
 */
class NuvioCollapsingTitleState {
    var maxPx by mutableFloatStateOf(0f)
    var offsetPx by mutableFloatStateOf(0f)
        private set
    val fraction: Float get() = if (maxPx <= 0f) 0f else (offsetPx / maxPx).coerceIn(0f, 1f)

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f || maxPx <= 0f) return Offset.Zero
            val before = offsetPx
            offsetPx = (before - available.y).coerceAtMost(maxPx)
            return Offset(0f, before - offsetPx)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (available.y <= 0f) return Offset.Zero
            val before = offsetPx
            offsetPx = (before - available.y).coerceAtLeast(0f)
            return Offset(0f, before - offsetPx)
        }
    }

    /** Opens the title fully again, e.g. when the screen scrolls back to its top by itself. */
    fun expand() {
        offsetPx = 0f
    }
}

@Composable
fun rememberNuvioCollapsingTitleState(): NuvioCollapsingTitleState = remember { NuvioCollapsingTitleState() }

/**
 * Folds this title away by [state]: it shrinks from the top and fades out as it goes. [keep] is left standing, for a
 * header that carries the status-bar space itself and has to keep covering it.
 */
fun Modifier.collapsingTitle(state: NuvioCollapsingTitleState, keep: Dp = 0.dp): Modifier =
    this
        .clipToBounds()
        .layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val foldable = (placeable.height - keep.roundToPx()).coerceAtLeast(0)
            state.maxPx = foldable.toFloat()
            val shift = state.offsetPx.roundToInt().coerceAtMost(foldable)
            layout(placeable.width, placeable.height - shift) { placeable.place(0, -shift) }
        }
        .graphicsLayer { alpha = (1f - state.fraction * 1.6f).coerceIn(0f, 1f) }
