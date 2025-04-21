package com.example.graphiclib.data


data class ChartNode(
    val id: String,
    val label: String,
    val value: Float,
    val children: List<ChartNode> = emptyList(),
    val color: String = "#3498db",
)

data class BarChartData(
    val rootNodes: List<ChartNode>,
    var currentLevel: List<ChartNode> = rootNodes,
    var title: String = "Bar Chart",
    var history: MutableList<List<ChartNode>> = mutableListOf(),
) {
    fun zoomIn(node: ChartNode) {
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
        fun generateSampleData(): BarChartData {
            val months2023 = listOf(
                ChartNode("jan-2023", "Янв 2023", 120.0f),
                ChartNode("feb-2023", "Фев 2023", 90.0f),
                ChartNode("mar-2023", "Мар 2023", 150.0f)
            )
            val months2024 = listOf(
                ChartNode("jan-2024", "Янв 2024", 180.0f),
                ChartNode("feb-2024", "Фев 2024", 110.0f)
            )

            val years = listOf(
                ChartNode("2023", "2023", 360.0f, months2023),
                ChartNode("2024", "2024", 290.0f, months2024)
            )

            return BarChartData(rootNodes = years)
        }
    }

}