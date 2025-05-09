package com.example.graphiclib.ui.lineChart

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.example.graphiclib.data.PointNode
import com.example.graphiclib.ui.base.ChartState
import com.example.graphiclib.ui.base.LineChartData

class LineChartState(
    data: LineChartData,
) : ChartState<PointNode, LineChartData>(data) {


    private val pointWidth by derivedStateOf { ceilWidth * 0.66f }
    val space by derivedStateOf { (ceilWidth - pointWidth) / 2 }


    override val sumValue by derivedStateOf {
        visibleItems.flatMap { it.values }.sumOf { it.toDouble() }.toFloat()
    }
    override val maxValue by derivedStateOf {
        visibleItems.flatMap { it.values }.maxOfOrNull { it } ?: 0f
    }
    override val minValue by derivedStateOf {
        visibleItems.flatMap { it.values }.minOfOrNull { it } ?: 0f
    }
    override val avgValue by derivedStateOf { sumValue / visibleCount }

    fun xOffset(point: PointNode) =
        chartWidth * visibleItems.indexOf(point).toFloat() / visibleCount.toFloat()

    fun yOffset(value: Float) = chartHeight * (maxValue - value) / (maxValue - minValue)

}