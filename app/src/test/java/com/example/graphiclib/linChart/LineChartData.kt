package com.example.graphiclib.linChart

import com.example.graphiclib.data.PointNode
import com.example.graphiclib.ui.base.ChartData
import org.junit.Test

class LineChartData {

    @Test
    fun createSampleData() {
        println(ChartData.default<PointNode>())
    }

    @Test
    fun childrenData() {
        println(ChartData.default<PointNode>().rootNodes[0].children[0].children)
    }

}