package com.example.graphiclib.ui.barChart

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.graphiclib.data.BarChartData
import com.example.graphiclib.data.BarNode
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

class BarChartState(
    data: BarChartData,
) {

    private var bars = data
    var currentLevel by mutableStateOf(data.currentLevel)
        private set
    val root = bars.rootNodes

    private val visibleBarCount by derivedStateOf { min(currentLevel.size, 4) }

    private var chartWidth = 0f
    private var chartHeight = 0f
    private val ceilWidth by derivedStateOf { floor(chartWidth / visibleBars.size) }
    val barWidth by derivedStateOf { ceilWidth * 0.66f }
    val space by derivedStateOf { (ceilWidth - barWidth) / 2 }
    val standardUnit by derivedStateOf {
        chartHeight / maxValue
    }

    val sumValue by derivedStateOf { visibleBars.sumOf { it.value.toDouble() }.toFloat() }
    val maxValue by derivedStateOf { visibleBars.maxOfOrNull { it.value } ?: 0f }
    val minValue by derivedStateOf { visibleBars.minOfOrNull { it.value } ?: 0f }
    val avgValue by derivedStateOf { sumValue / visibleBarCount }

    private var scrollOffset by mutableFloatStateOf(0f)
    val scrollableState = ScrollableState {
        scrollOffset = if (it > 0) {
            (scrollOffset - it.scrolledBars).coerceAtLeast(0f)
        } else {
            (scrollOffset - it.scrolledBars).coerceAtMost(
                (currentLevel.size - visibleBarCount).coerceAtLeast(
                    0
                ).toFloat()
            )
        }
        it
    }

    private val Float.scrolledBars: Float
        get() = this * visibleBarCount.toFloat() / chartWidth

    val visibleBars by derivedStateOf {
        if (currentLevel.isNotEmpty()) {
            currentLevel.subList(
                scrollOffset.roundToInt().coerceAtLeast(0),
                (scrollOffset.roundToInt() + visibleBarCount).coerceAtMost(currentLevel.size)
            )
        } else {
            emptyList()
        }
    }

    val clickableRectangles by derivedStateOf {
        val rectangles = mutableListOf<BarRectangle>()
        visibleBars.forEachIndexed { index, bar ->
            val xOffset = xOffset(bar) + space
            val yOffset = chartHeight - bar.value * standardUnit
            rectangles.add(
                BarRectangle(
                    index,
                    Offset(xOffset, yOffset),
                    Offset(xOffset + barWidth, chartHeight),
                )
            )
        }
        rectangles
    }

    fun setChartSize(width: Float, height: Float) {
        chartWidth = width
        chartHeight = height
    }

    fun xOffset(bar: BarNode) =
        chartWidth * visibleBars.indexOf(bar).toFloat() / visibleBarCount.toFloat()

    fun yOffset(value: Float) = chartHeight * (maxValue - value) / (maxValue - minValue)

    fun zoomIn(node: BarNode) {
        bars.zoomIn(node)
        currentLevel = bars.currentLevel
        scrollOffset = 0f
    }

    fun zoomOut() {
        bars.zoomOut()
        currentLevel = bars.currentLevel
        scrollOffset = 0f
    }

}

data class BarRectangle(
    val id: Int,
    val topLeft: Offset,
    val bottomRight: Offset,
) {
    fun contains(point: Offset): Boolean =
        point.x in topLeft.x..bottomRight.x && point.y in topLeft.y..bottomRight.y
}