package com.example.graphiclib.linChart

import com.example.graphiclib.data.LineChartData
import org.junit.Test

class LineChartData {

    @Test
    fun createSampleData() {
        println(LineChartData.sampleHierarchy())
    }

    @Test
    fun childrenData() {
        println(LineChartData.sampleHierarchy().rootNodes[0].children[0].children)
    }

}