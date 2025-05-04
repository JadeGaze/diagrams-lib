package com.example.graphiclib

import com.example.graphiclib.data.BarChartData
import org.junit.Test

class DataZoomInTest {

    @Test
    fun zoomIn_isCorrect() {
        val chartData = BarChartData.generateLineSampleData()

        chartData.zoomIn(chartData.currentLevel[0])
        println(chartData.currentLevel)

        chartData.zoomOut()
        println(chartData.currentLevel)
    }

}