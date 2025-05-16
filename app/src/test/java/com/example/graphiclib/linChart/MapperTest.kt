package com.example.graphiclib.linChart

import com.example.graphiclib.data.RawDataPoint
import org.junit.Test

class MapperTest {
    @Test
    fun mapper() {
        val testData = listOf(
            RawDataPoint("2024-01-01T08:00:00", 300f),
            RawDataPoint("2023-01-01T09:00:00", 100f),
            RawDataPoint("2023-01-01T14:00:00", 200f),
            RawDataPoint("2023-01-02T10:00:00", 150f),
            RawDataPoint("2023-02-15T11:00:00", 180f)
        )

//        val chartData = buildHierarchyFromFlatData(testData, TimeResolution.YEAR)
//
//        // Печатаем структуру
//        fun printTree(nodes: List<PointNode>, indent: String = "") {
//            nodes.forEach { node ->
//                println("$indent${node.label} (${node.values})")
//                printTree(node.children, "$indent  ")
//            }
//        }
//
//        printTree(chartData.rootNodes)
    }
}