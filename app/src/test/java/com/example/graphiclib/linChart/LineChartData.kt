package com.example.graphiclib.linChart

import com.example.graphiclib.data.PointNode
import com.example.graphiclib.data.generateLargeLineChartData
import com.example.graphiclib.ui.base.ChartData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.measureTime

class LineChartData {

    @Test
    fun createSampleData() {
        println(ChartData.default<PointNode>())
    }

    @Test
    fun childrenData() {
        println(ChartData.default<PointNode>().rootNodes[0].children[0].children)
    }

    @Test
    fun bigDataSet() {

        val result = measureTime {
            runBlocking { generateLargeLineChartData() }
        }

        println("RESULT: $result")
    }

}