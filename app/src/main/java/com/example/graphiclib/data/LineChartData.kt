package com.example.graphiclib.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


data class PointNode(
    val id: String,
    val values: List<Float>,
    val label: String,
    val children: List<PointNode> = emptyList(),
)

data class LineChartData(
    val rootNodes: List<PointNode>,
    var currentLevel: List<PointNode> = rootNodes,
    var title: String = "Point Chart",
    var history: MutableList<List<PointNode>> = mutableListOf(),
) {
    fun zoomIn(node: PointNode) {
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
        fun sampleHierarchy(): LineChartData {
            val year2020 = PointNode(
                id = "2020",
                values = listOf(300f, 350f, 280f), // 3 значения: утро, день, вечер
                label = "2020 год",
                children = listOf(
                    PointNode(
                        id = "2020_q1",
                        values = listOf(400f, 420f, 380f),
                        label = "Q1 2020",
                        children = listOf(
                            PointNode("2020_q1_jan", listOf(200f, 220f, 190f), "Январь"),
                            PointNode("2020_q1_feb", listOf(340f, 360f, 320f), "Февраль"),
                            PointNode("2020_q1_mar", listOf(150f, 170f, 130f), "Март"),
                            PointNode("2020_q1_apr", listOf(170f, 190f, 150f), "Апрель")
                        )
                    ),
                    PointNode(
                        id = "2020_q2",
                        values = listOf(350f, 370f, 330f),
                        label = "Q2 2020"
                    )
                )
            )

            return LineChartData(
                rootNodes = listOf(year2020),
                title = "Продажи по времени суток"
            )
        }

        fun getTestData(): List<RawDataPoint> = listOf(
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
        )
    }
}

data class RawDataPoint(
    val timestamp: String,
    val value: Float,
)

// Уровни детализации
enum class TimeResolution(
    val title: String,
    val canDrillDown: Boolean,
) {
    YEAR("Год", true),
    MONTH("Месяц", true),
    DAY("День", true),
    HOUR("Час", false);

    fun next() = when (this) {
        YEAR -> MONTH
        MONTH -> DAY
        DAY -> HOUR
        HOUR -> HOUR
    }
}

// Основной метод преобразования
fun buildHierarchyFromFlatData(
    rawData: List<RawDataPoint>,
    timeResolution: TimeResolution = TimeResolution.YEAR,
    maxPointsPerNode: Int = 50,
): LineChartData {

    val parsedData = rawData.map { point ->
        ParsedDataPoint(
            timestamp = LocalDateTime.parse(
                point.timestamp,
                DateTimeFormatter.ISO_DATE_TIME
            ),
            value = point.value
        )
    }.sortedBy { it.timestamp }

    val rootNodes = buildTree(
        data = parsedData,
        currentResolution = timeResolution,
        maxPointsPerNode = maxPointsPerNode
    )

    return LineChartData(
        rootNodes = rootNodes,
        title = "Данные за ${timeResolution.title}"
    )
}

// Внутренний класс для парсинга
private data class ParsedDataPoint(
    val timestamp: LocalDateTime,
    val value: Float,
)

private fun buildTree(
    data: List<ParsedDataPoint>,
    currentResolution: TimeResolution,
    maxPointsPerNode: Int,
): List<PointNode> {
    if (data.isEmpty()) return emptyList()

    // Группируем данные по текущему уровню
    val grouped = when (currentResolution) {
        TimeResolution.YEAR -> data.groupBy { it.timestamp.year }
        TimeResolution.MONTH -> data.groupBy { YearMonth.from(it.timestamp) }
        TimeResolution.DAY -> data.groupBy { it.timestamp.toLocalDate() }
        TimeResolution.HOUR -> data.groupBy {
            it.timestamp.toLocalDate() to it.timestamp.hour
        }
    }

    return grouped.map { (key, group) ->
        val values = group.map { it.value }
        val aggregatedValues = if (values.size > maxPointsPerNode) {
            aggregateValues(values, maxPointsPerNode)
        } else {
            values
        }
        PointNode(
            id = when (key) {
                is Number -> key.toString()
                is YearMonth -> "${key.year}-${key.monthValue}"
                is LocalDate -> key.toString()
                is Pair<*, *> -> "${key.first}_${key.second}"
                else -> key.toString()
            },
            values = aggregatedValues,
            label = createLabel(key, currentResolution),
            children = if (currentResolution.canDrillDown) {
                buildTree(
                    data = group,
                    currentResolution = currentResolution.next(),
                    maxPointsPerNode = maxPointsPerNode
                )
            } else {
                emptyList()
            }
        )
    }
}

private fun createLabel(key: Any, resolution: TimeResolution): String {
    return when (resolution) {
        TimeResolution.YEAR -> "$key год"
        TimeResolution.MONTH -> {
            val ym = key as YearMonth
            ym.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale("ru")))
        }

        TimeResolution.DAY -> {
            val date = key as LocalDate
            date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("ru")))
        }

        TimeResolution.HOUR -> {
            val (date, hour) = key as Pair<*, *>
            "${(date as LocalDate).format(DateTimeFormatter.ISO_DATE)} ${
                hour.toString().padStart(2, '0')
            }:00"
        }
    }
}

private fun aggregateValues(
    values: List<Float>,
    maxPoints: Int,
): List<Float> {
    if (values.size <= maxPoints) return values
    return when {
        values.size > maxPoints * 2 -> {
            val chunkSize = values.size / maxPoints
            values.chunked(chunkSize) { chunk ->
                listOf(
                    chunk.average().toFloat(),
                    chunk.max(),
                    chunk.min()
                ).random()
            }.distinct().take(maxPoints)
        }
        else -> {
            values.chunked(values.size / maxPoints) { chunk ->
                chunk.average()
            }.map { it.toFloat() }
        }
    }
}