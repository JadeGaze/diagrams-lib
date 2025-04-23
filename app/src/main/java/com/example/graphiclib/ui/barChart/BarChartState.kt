package com.example.graphiclib.ui.barChart

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.graphiclib.data.BarChartData
import com.example.graphiclib.data.BarNode
import kotlin.math.floor
import kotlin.math.min

class BarChartState(
    data: BarChartData,
) {

    private var bars = data
    var currentLevel by mutableStateOf(data.currentLevel)
        private set

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

    val visibleBars by derivedStateOf {
        if (currentLevel.isNotEmpty()) {
            currentLevel.subList(0, visibleBarCount)
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
        Log.d("clickableRectangles", "$rectangles")
        rectangles
    }

    val root = bars.rootNodes

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
        Log.d("STATE", "ZOOM IN; ${currentLevel}")
    }

    fun zoomOut() {
        bars.zoomOut()
        currentLevel = bars.currentLevel
        Log.d("STATE", "ZOOM OUT; ${currentLevel}")
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