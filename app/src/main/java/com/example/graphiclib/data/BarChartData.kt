package com.example.graphiclib.data


data class BarNode(
    val id: String,
    val label: String,
    val value: Float,
    val children: List<BarNode> = emptyList(),
    val color: String = "#3498db",
)

data class BarChartData(
    val rootNodes: List<BarNode>,
    var currentLevel: List<BarNode> = rootNodes,
    var title: String = "Bar Chart",
    var history: MutableList<List<BarNode>> = mutableListOf(),
) {
    fun zoomIn(node: BarNode) {
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

            return BarChartData(rootNodes = years)
        }
    }

}