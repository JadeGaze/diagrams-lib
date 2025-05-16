package com.example.graphiclib.ui.base

import com.example.graphiclib.data.BarNode
import com.example.graphiclib.data.PointNode
import com.example.graphiclib.data.RawDataPoint
import com.example.graphiclib.data.TimeResolution
import com.example.graphiclib.data.buildHierarchyFromFlatData

interface TreeNode<T> {
    val id: String
    val label: String
    val children: List<T>
}

data class ChartData<T : TreeNode<T>>(
    val rootNodes: List<T>,
    var currentLevel: List<T> = rootNodes,
    var title: String = "Chart",
    var history: MutableList<List<T>> = mutableListOf(),
) {
    fun zoomIn(node: T) {
        if (node.children.isNotEmpty()) {
            history.add(currentLevel)
            currentLevel = node.children
            title = "Данные за: ${node.label}"
        }
    }

    fun zoomOut() {
        if (history.isNotEmpty()) {
            currentLevel = history.removeAt(history.lastIndex)
            title = if (history.isEmpty()) "Общий обзор"
            else "Данные за: ${currentLevel.firstOrNull()?.label}"
        }
    }

    companion object {
        suspend inline fun <reified T : TreeNode<T>> default(): ChartData<T> {
            return when (T::class) {
                BarNode::class -> {
                    val months2023 = listOf(
                        BarNode("jan-2023", "Янв 2023", 120.0f),
                        BarNode("feb-2023", "Фев 2023", 90.0f),
                        BarNode("mar-2023", "Мар 2023", 150.0f),
                        BarNode("apr-2023", "Апр 2023", 170.0f),
                        BarNode("may-2023", "Май 2023", 200.0f),
                        BarNode("jun-2023", "Июнь 2023", 250.0f),
                        BarNode("jul-2023", "Июль 2023", 350.0f),
                        BarNode("aug-2023", "Авг 2023", 400.0f),
                        BarNode("sep-2023", "Сент 2023", 320.0f),
                        BarNode("oct-2023", "Окт 2023", 130.0f),
                        BarNode("nov-2023", "Нояб 2023", 230.0f),
                        BarNode("dec-2023", "Дек 2023", 150.0f),
                    )
                    val months2024 = listOf(
                        BarNode("jan-2024", "Янв 2024", 180.0f),
                        BarNode("feb-2024", "Фев 2024", 110.0f)
                    )

                    val years = listOf(
                        BarNode("2023", "2023", 360.0f, months2023),
                        BarNode("2024", "2024", 290.0f, months2024)
                    )

                    BarChartData(rootNodes = years) as ChartData<T>
                }

                PointNode::class -> {
                    return buildHierarchyFromFlatData(
                        rawData = listOf(
                            RawDataPoint("2023-01-01T00:00", 10f),
                            RawDataPoint("2023-01-01T01:00", 12f),
                            RawDataPoint("2023-01-01T02:00", 8f),
                            RawDataPoint("2023-01-01T03:00", 15f),
                            RawDataPoint("2023-01-01T04:00", 20f),
                            RawDataPoint("2023-01-01T05:00", 18f),
                            RawDataPoint("2023-01-01T06:00", 25f),
                            RawDataPoint("2023-01-01T07:00", 30f),
                            RawDataPoint("2023-01-01T08:00", 28f),
                            RawDataPoint("2023-01-01T09:00", 22f),
                            RawDataPoint("2023-01-01T10:00", 35f),
                            RawDataPoint("2023-01-01T11:00", 40f),

                            RawDataPoint("2023-01-02T00:00", 15f),
                            RawDataPoint("2023-01-02T01:00", 18f),
                            RawDataPoint("2023-01-02T02:00", 10f),
                            RawDataPoint("2023-01-02T03:00", 22f),
                            RawDataPoint("2023-01-02T04:00", 25f),
                            RawDataPoint("2023-01-02T05:00", 20f),
                            RawDataPoint("2023-01-02T06:00", 32f),
                            RawDataPoint("2023-01-02T07:00", 35f),
                            RawDataPoint("2023-01-02T08:00", 30f),
                            RawDataPoint("2023-01-02T09:00", 28f),
                            RawDataPoint("2023-01-02T10:00", 42f),
                            RawDataPoint("2023-01-02T11:00", 38f),

                            RawDataPoint("2023-01-03T00:00", 20f),
                            RawDataPoint("2023-01-03T01:00", 22f),
                            RawDataPoint("2023-01-03T02:00", 15f),
                            RawDataPoint("2023-01-03T03:00", 25f),
                            RawDataPoint("2023-01-03T04:00", 30f),
                            RawDataPoint("2023-01-03T05:00", 25f),
                            RawDataPoint("2023-01-03T06:00", 38f),
                            RawDataPoint("2023-01-03T07:00", 40f),
                            RawDataPoint("2023-01-03T08:00", 35f),
                            RawDataPoint("2023-01-03T09:00", 32f),
                            RawDataPoint("2023-01-03T10:00", 45f),
                            RawDataPoint("2023-01-03T11:00", 42f),

                            RawDataPoint("2023-01-04T00:00", 25f),
                            RawDataPoint("2023-01-04T01:00", 28f),
                            RawDataPoint("2023-01-04T02:00", 18f),
                            RawDataPoint("2023-01-04T03:00", 30f),
                            RawDataPoint("2023-01-04T04:00", 35f),
                            RawDataPoint("2023-01-04T05:00", 30f),
                            RawDataPoint("2023-01-04T06:00", 42f),
                            RawDataPoint("2023-01-04T07:00", 45f),
                            RawDataPoint("2023-01-04T08:00", 40f),
                            RawDataPoint("2023-01-04T09:00", 38f),
                            RawDataPoint("2023-01-04T10:00", 48f),
                            RawDataPoint("2023-01-04T11:00", 45f),

                            RawDataPoint("2023-01-05T00:00", 30f),
                            RawDataPoint("2023-01-05T01:00", 32f),
                            RawDataPoint("2023-01-05T02:00", 25f),
                            RawDataPoint("2023-01-05T03:00", 35f),
                            RawDataPoint("2023-01-05T04:00", 40f),
                            RawDataPoint("2023-01-05T05:00", 35f),
                            RawDataPoint("2023-01-05T06:00", 45f),
                            RawDataPoint("2023-01-05T07:00", 50f),
                            RawDataPoint("2023-01-05T08:00", 45f),
                            RawDataPoint("2023-01-05T09:00", 42f),
                            RawDataPoint("2023-01-05T10:00", 50f),
                            RawDataPoint("2023-01-05T11:00", 45f)
                        ),
                        timeResolution = TimeResolution.MONTH,
                        maxPointsPerNode = 50
                    ) as ChartData<T>
                }

                else -> throw IllegalArgumentException("Unsupported chart type")
            }
        }
    }
}

typealias BarChartData = ChartData<BarNode>
typealias LineChartData = ChartData<PointNode>