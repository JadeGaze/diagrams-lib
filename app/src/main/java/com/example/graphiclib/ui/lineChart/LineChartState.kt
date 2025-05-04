package com.example.graphiclib.ui.lineChart

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.graphiclib.data.LineChartData
import com.example.graphiclib.data.PointNode
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

class LineChartState(
    data: LineChartData,
) {
    private val points = data
    var currentLevel by mutableStateOf(data.currentLevel)
        private set
    val root = points.rootNodes

    private val visiblePointCount by derivedStateOf { min(currentLevel.size, 4) }

    private var chartWidth = 0f
    private var chartHeight = 0f
    private val ceilWidth by derivedStateOf { floor(chartWidth / visiblePoints.size) }
    val pointWidth by derivedStateOf { ceilWidth * 0.66f }
    val space by derivedStateOf { (ceilWidth - pointWidth) / 2 }
    val standardUnit by derivedStateOf {
        chartHeight / maxValue
    }

    val sumValue by derivedStateOf { visiblePoints.flatMap { it.values }.sumOf { it.toDouble() }.toFloat() }
    val maxValue by derivedStateOf { visiblePoints.flatMap { it.values }.maxOfOrNull { it } ?: 0f }
    val minValue by derivedStateOf { visiblePoints.flatMap { it.values }.minOfOrNull { it } ?: 0f }
    val avgValue by derivedStateOf { sumValue / visiblePointCount }

    private var scrollOffset by mutableFloatStateOf(0f)
    val scrollableState = ScrollableState {
        scrollOffset = if (it > 0) {
            (scrollOffset - it.scrolledPoints).coerceAtLeast(0f)
        } else {
            (scrollOffset - it.scrolledPoints).coerceAtMost(
                (currentLevel.size - visiblePointCount).coerceAtLeast(
                    0
                ).toFloat()
            )
        }
        it
    }

    private val Float.scrolledPoints: Float
        get() = this * visiblePointCount.toFloat() / chartWidth

    val visiblePoints by derivedStateOf {
        if (currentLevel.isNotEmpty()) {
            currentLevel.subList(
                scrollOffset.roundToInt().coerceAtLeast(0),
                (scrollOffset.roundToInt() + visiblePointCount).coerceAtMost(currentLevel.size)
            )
        } else {
            emptyList()
        }
    }

    fun setChartSize(width: Float, height: Float) {
        chartWidth = width
        chartHeight = height
    }

    fun xOffset(point: PointNode) =
        chartWidth * visiblePoints.indexOf(point).toFloat() / visiblePointCount.toFloat()

    fun yOffset(value: Float) = chartHeight * (maxValue - value) / (maxValue - minValue)

    fun zoomIn(node: PointNode) {
        points.zoomIn(node)
        currentLevel = points.currentLevel
        scrollOffset = 0f
    }

    fun zoomOut() {
        points.zoomOut()
        currentLevel = points.currentLevel
        scrollOffset = 0f
    }

}