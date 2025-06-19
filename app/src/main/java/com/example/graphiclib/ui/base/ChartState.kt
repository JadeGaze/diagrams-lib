package com.example.graphiclib.ui.base

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

abstract class ChartState<T : TreeNode<T>, D : ChartData<T>>(
    protected val data: D,
){
    val root = data.rootNodes

    protected var chartWidth = 0f
        private set
    protected var chartHeight = 0f
        private set

    abstract val sumValue: Float
    abstract val maxValue: Float
    abstract val minValue: Float
    abstract val avgValue: Float

    val standardUnit by derivedStateOf {
        chartHeight / maxValue
    }

    fun setChartSize(width: Float, height: Float) {
        chartWidth = width
        chartHeight = height
    }

    var currentLevel by mutableStateOf(data.currentLevel)
        protected set
    protected val visibleCount by derivedStateOf { min(currentLevel.size, 4) }

    protected val ceilWidth by derivedStateOf { floor(chartWidth / visibleItems.size) }

    val visibleItems by derivedStateOf {
        if (currentLevel.isNotEmpty()) {
            currentLevel.subList(
                scrollOffset.roundToInt().coerceAtLeast(0),
                (scrollOffset.roundToInt() + visibleCount).coerceAtMost(currentLevel.size)
            )
        } else {
            emptyList()
        }
    }

    private val Float.scrolledPoints: Float
        get() = this * visibleCount.toFloat() / chartWidth


    protected var scrollOffset by mutableFloatStateOf(0f)
    val scrollableState = ScrollableState {
        scrollOffset = if (it > 0) {
            (scrollOffset - it.scrolledPoints).coerceAtLeast(0f)
        } else {
            (scrollOffset - it.scrolledPoints).coerceAtMost(
                (currentLevel.size - visibleCount).coerceAtLeast(
                    0
                ).toFloat()
            )
        }
        it
    }


    fun zoomIn(node: T) {
        data.zoomIn(node)
        currentLevel = data.currentLevel
        scrollOffset = 0f
    }

    fun zoomOut() {
        data.zoomOut()
        currentLevel = data.currentLevel
        scrollOffset = 0f
    }


    protected open val visibleItemCount by derivedStateOf { min(currentLevel.size, 4) }

}
