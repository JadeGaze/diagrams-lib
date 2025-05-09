package com.example.graphiclib.ui.barChart

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import com.example.graphiclib.data.BarNode
import com.example.graphiclib.ui.base.BarChartData
import com.example.graphiclib.ui.base.ChartState
import kotlin.math.min

class BarChartState(
    data: BarChartData,
) : ChartState<BarNode, BarChartData>(data) {

    private val visibleBarCount by derivedStateOf { min(currentLevel.size, 4) }

    val barWidth by derivedStateOf { ceilWidth * 0.66f }
    val space by derivedStateOf { (ceilWidth - barWidth) / 2 }

    override val sumValue by derivedStateOf { visibleItems.sumOf { it.value.toDouble() }.toFloat() }
    override val maxValue by derivedStateOf { visibleItems.maxOfOrNull { it.value } ?: 0f }
    override val minValue by derivedStateOf { visibleItems.minOfOrNull { it.value } ?: 0f }
    override val avgValue by derivedStateOf { sumValue / visibleBarCount }

    val clickableRectangles by derivedStateOf {
        val rectangles = mutableListOf<BarRectangle>()
        visibleItems.forEachIndexed { index, bar ->
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

    fun xOffset(bar: BarNode) =
        chartWidth * visibleItems.indexOf(bar).toFloat() / visibleBarCount.toFloat()

    fun yOffset(value: Float) = chartHeight * (maxValue - value) / (maxValue - minValue)

}

data class BarRectangle(
    val id: Int,
    val topLeft: Offset,
    val bottomRight: Offset,
) {
    fun contains(point: Offset): Boolean =
        point.x in topLeft.x..bottomRight.x && point.y in topLeft.y..bottomRight.y
}