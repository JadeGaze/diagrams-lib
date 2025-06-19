package com.example.graphiclib.data

import com.example.graphiclib.ui.base.LineChartData
import com.example.graphiclib.ui.base.TreeNode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random


data class PointNode(
    override val id: String,
    override val label: String,
    val values: List<Float>,
    override val children: List<PointNode> = emptyList(),
) : TreeNode<PointNode>

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
    val start = System.currentTimeMillis()

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
    println("TIME GENERATION: ${(System.currentTimeMillis() - start)}")
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

fun generateLargeLineChartData(): LineChartData {


    val startDate = LocalDateTime.of(2023, 1, 1, 0, 0)
    val endDate = LocalDateTime.of(2023, 12, 31, 23, 0)
    val totalHours = ChronoUnit.HOURS.between(startDate, endDate) + 1

    // Генерируем 20,000 точек (примерно по 2 точки в час)
    val pointsPerHour = 2
    val totalPoints = 20_000
    val step = (totalHours * pointsPerHour).toDouble() / totalPoints

    val rawData = mutableListOf<RawDataPoint>()
    var currentDateTime = startDate
    var hourFraction = 0.0

    // Базовые параметры для генерации "реалистичных" данных
    var baseValue = 50f
    val dailyVariation = 30f
    val hourlyVariation = 15f
    val randomNoise = 5f

    repeat(totalPoints) {
        // Добавляем точку
        rawData.add(
            RawDataPoint(
                timestamp = currentDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                value = baseValue +
                        (dailyVariation * sin(2 * PI * currentDateTime.dayOfYear / 365)).toFloat() +
                        (hourlyVariation * sin(2 * PI * currentDateTime.hour / 24)).toFloat() +
                        (Random.nextFloat() * 2 - 1) * randomNoise
            )
        )

        // Перемещаем время вперед
        hourFraction += step
        if (hourFraction >= 1.0) {
            val fullHours = hourFraction.toInt()
            currentDateTime = currentDateTime.plusHours(fullHours.toLong())
            hourFraction -= fullHours

            // Медленно увеличиваем базовое значение с небольшими случайными колебаниями
            baseValue += (Random.nextFloat() * 2 - 0.5f)
        }
    }

    println("size: ${rawData.size}")

    return buildHierarchyFromFlatData(
        rawData = rawData,
        timeResolution = TimeResolution.YEAR,
        maxPointsPerNode = 50
    )
}