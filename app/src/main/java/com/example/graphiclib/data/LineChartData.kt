package com.example.graphiclib.data

import com.example.graphiclib.ui.base.LineChartData
import com.example.graphiclib.ui.base.TreeNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

private val labelCache = ConcurrentHashMap<Pair<Any, TimeResolution>, String>()

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


suspend fun buildHierarchyFromFlatData(
    rawData: List<RawDataPoint>,
    timeResolution: TimeResolution = TimeResolution.YEAR,
    maxPointsPerNode: Int = 50,
): LineChartData = coroutineScope {
    val isAlreadySorted = rawData.asSequence()
        .zipWithNext { a, b -> a.timestamp <= b.timestamp }
        .all { it }
    println(isAlreadySorted)
    var parsedData = if (rawData.size > 1000) {
        rawData.chunked(rawData.size / Runtime.getRuntime().availableProcessors())
            .map { chunk ->
                async(Dispatchers.Default) {
                    chunk.map { point ->
                        ParsedDataPoint(
                            timestamp = parseTimestamp(point.timestamp),
                            value = point.value
                        )
                    }
                }
            }.awaitAll().flatten()
    } else {
        rawData.map { point ->
            async(Dispatchers.Default) {
                ParsedDataPoint(
                    timestamp = parseTimestamp(point.timestamp),
                    value = point.value
                )
            }
        }.awaitAll()
    }


    if (!isAlreadySorted) {
        parsedData = parsedData.sortedWith(TimestampComparator)
    }

    // 1. Оптимизация парсинга и сортировки
//    val parsedData = if (rawData.size > 1000) {
//
//        // Параллельный парсинг для больших наборов данных
//        rawData.chunked(rawData.size / Runtime.getRuntime().availableProcessors())
//            .map { chunk ->
//                async(Dispatchers.Default) {
//                    chunk.map { point ->
//                        ParsedDataPoint(
//                            timestamp = parseTimestampFast(point.timestamp),
//                            value = point.value
//                        )
//                    }
//                }
//            }.awaitAll().flatten()
//    } else {
//        rawData.map { point ->
//            ParsedDataPoint(
//                timestamp = parseTimestampFast(point.timestamp),
//                value = point.value
//            )
//        }
//    }.sortedWith(TimestampComparator)

    val rootNodes = buildTreeFlow(
        data = parsedData,
        currentResolution = timeResolution,
        maxPointsPerNode = maxPointsPerNode
    ).toList()

    return@coroutineScope LineChartData(
        rootNodes = rootNodes,
        title = "Данные за ${timeResolution.title}"
    )
}

// Оптимизированный парсер временных меток
private fun parseTimestampFast(timestampStr: String): Long {
    try {
        val normalized = timestampStr.substring(0 until minOf(19, timestampStr.length))

        return LocalDateTime.of(
            normalized.substring(0, 4).toInt(),
            normalized.substring(5, 7).toInt(),
            normalized.substring(8, 10).toInt(),
            normalized.substring(11, 13).toIntOrNull() ?: 0,
            normalized.substring(14, 16).toIntOrNull() ?: 0,
            normalized.substring(17, 19).toIntOrNull() ?: 0
        ).toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    } catch (e: Exception) {
        return LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }
}

// Компаратор для быстрой сортировки
private object TimestampComparator : Comparator<ParsedDataPoint> {
    override fun compare(a: ParsedDataPoint, b: ParsedDataPoint): Int {
        return when {
            a.timestamp < b.timestamp -> -1
            a.timestamp > b.timestamp -> 1
            else -> 0
        }
    }
}

// Внутренний класс для парсинга
private data class ParsedDataPoint(
    val timestamp: Long,
    val value: Float,
)


private val monthNames = arrayOf(
    "янв", "фев", "мар", "апр", "май", "июн",
    "июл", "авг", "сен", "окт", "ноя", "дек"
)

private fun createLabel(timestamp: Long, resolution: TimeResolution): String {
    // Разбираем timestamp без создания Calendar
    val (year, month, day, hour) = extractDateParts(timestamp)

    return when (resolution) {
        TimeResolution.YEAR -> buildString(8) {
            append(year)
            append(" год")
        }

        TimeResolution.MONTH -> buildString(9) {
            append(monthNames[month - 1])
            append(' ')
            append(year)
        }

        TimeResolution.DAY -> buildString(11) {
            append(day)
            append(' ')
            append(monthNames[month - 1])
            append(' ')
            append(year)
        }

        TimeResolution.HOUR -> buildString(16) {
            append(year)
            append('-')
            append(month.toString().padStart(2, '0'))
            append('-')
            append(day.toString().padStart(2, '0'))
            append(' ')
            append(hour.toString().padStart(2, '0'))
            append(":00")
        }
    }
}

private fun extractDateParts(timestamp: Long): DateParts {
    val seconds = timestamp / 1000
    val days = (seconds / 86400).toInt()
    val rem = (seconds % 86400).toInt()

    // Алгоритм вычисления даты из дней (упрощенная версия)
    var tempDays = days + 719468
    val era = (if (tempDays >= 0) tempDays else tempDays - 146096) / 146097
    val doe = tempDays - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val year = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val day = doy - (153 * mp + 2) / 5 + 1
    val month = mp + if (mp < 10) 3 else -9

    return DateParts(
        year = if (month <= 2) year + 1 else year,
        month = month,
        day = day,
        hour = rem / 3600
    )
}

private data class DateParts(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
)

private suspend fun buildTreeFlow(
    data: List<ParsedDataPoint>,
    currentResolution: TimeResolution,
    maxPointsPerNode: Int,
): Flow<PointNode> = flow {
    if (data.isEmpty()) return@flow

    val grouped = parallelGroupBy(data, currentResolution)


    val parallelism = when {
        data.size > 5000 -> 8
        data.size > 1000 -> 4
        else -> 2
    }

    coroutineScope {
        grouped.entries.chunked(parallelism).forEach { chunk ->
            val nodes = chunk.map { (key, group) ->
                async(Dispatchers.Default) {
                    processGroupOptimized(key, group, currentResolution, maxPointsPerNode)
                }
            }.awaitAll()

            nodes.forEach { emit(it) }
        }
    }
}

private suspend fun processGroupOptimized(
    key: Long,
    group: List<ParsedDataPoint>,
    currentResolution: TimeResolution,
    maxPointsPerNode: Int,
): PointNode {
    val values = FloatArray(group.size) { group[it].value }

    val aggregatedValues = if (values.size > maxPointsPerNode) {
        aggregateValues(values, maxPointsPerNode)
    } else {
        values.toList()
    }

    return PointNode(
        id = key.toString(),
        values = aggregatedValues,
        label = createLabel(key, currentResolution),
        children = if (currentResolution.canDrillDown) {
            buildTreeFlow(group, currentResolution.next(), maxPointsPerNode).toList()
        } else {
            emptyList()
        }
    )
}

private suspend fun aggregateValues(values: FloatArray, maxPoints: Int): List<Float> =
    withContext(Dispatchers.Default) {
        when {
            values.size <= maxPoints -> values.toList()
            else -> {
                val step = values.size.toFloat() / maxPoints.toFloat()
                FloatArray(maxPoints) { index ->
                    val start = (index * step).toInt()
                    val end = ((index + 1) * step).toInt().coerceAtMost(values.size)
                    values.slice(start until end).average().toFloat()
                }.toList()
            }
        }
    }


// --------------------------------------------------
private suspend fun parallelGroupBy(
    data: List<ParsedDataPoint>,
    resolution: TimeResolution,
): Map<Long, List<ParsedDataPoint>> = coroutineScope {
    val result = ConcurrentHashMap<Long, MutableList<ParsedDataPoint>>()
    val chunkSize = (data.size + Runtime.getRuntime().availableProcessors() - 1) /
            Runtime.getRuntime().availableProcessors()

    data.chunked(chunkSize).map { chunk ->
        async(Dispatchers.Default) {
            val localMap = mutableMapOf<Long, MutableList<ParsedDataPoint>>()
            for (item in chunk) {
                val key = getGroupKey(item.timestamp, resolution)
                localMap.getOrPut(key) { ArrayList(128) }.add(item)
            }
            result.mergeAll(localMap)
        }
    }.awaitAll()
    result
}

private fun ConcurrentHashMap<Long, MutableList<ParsedDataPoint>>.mergeAll(
    localMap: Map<Long, MutableList<ParsedDataPoint>>,
) {
    localMap.forEach { (key, values) ->
        this.merge(key, values) { old, new -> old.also { it.addAll(new) } }
    }
}

private fun getGroupKey(timestamp: Long, resolution: TimeResolution): Long {
    return when (resolution) {
        TimeResolution.YEAR -> timestamp - timestamp % 31556952000L
        TimeResolution.MONTH -> {
            val days = timestamp / 86400000
            (days - days % 30) * 86400000
        }

        TimeResolution.DAY -> timestamp - timestamp % 86400000
        TimeResolution.HOUR -> timestamp - timestamp % 3600000
    }
}

private fun parseTimestamp(timestampStr: String): Long {
    return when {
        timestampStr.length >= 19 -> {
            val year = (timestampStr[0] - '0') * 1000 +
                    (timestampStr[1] - '0') * 100 +
                    (timestampStr[2] - '0') * 10 +
                    (timestampStr[3] - '0')

            val month = (timestampStr[5] - '0') * 10 + (timestampStr[6] - '0')
            val day = (timestampStr[8] - '0') * 10 + (timestampStr[9] - '0')
            val hour = (timestampStr[11] - '0') * 10 + (timestampStr[12] - '0')
            val minute = (timestampStr[14] - '0') * 10 + (timestampStr[15] - '0')
            val second = (timestampStr[17] - '0') * 10 + (timestampStr[18] - '0')

            LocalDateTime.of(year, month, day, hour, minute, second)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        }

        else -> parseTimestampFast(timestampStr)
    }
}