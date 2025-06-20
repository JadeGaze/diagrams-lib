package com.example.graphiclib

import com.example.graphiclib.data.BarNode
import com.example.graphiclib.ui.base.BarChartData
import com.example.graphiclib.ui.base.ChartData
import org.junit.Test

class DataZoomInTest {

    @Test
    fun zoomIn_isCorrect() {
        val chartData = ChartData.default<BarNode>()

        chartData.zoomIn(chartData.currentLevel[0])
        println(chartData.currentLevel)

        chartData.zoomOut()
        println(chartData.currentLevel)
    }

}