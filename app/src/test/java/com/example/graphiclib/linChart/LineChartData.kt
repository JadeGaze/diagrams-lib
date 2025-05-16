package com.example.graphiclib.linChart

import com.example.graphiclib.data.RawDataPoint
import com.example.graphiclib.data.TimeResolution
import com.example.graphiclib.data.buildHierarchyFromFlatData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.measureTime

class LineChartData {

    @Test
    fun createSampleData() {

        val startDate = LocalDateTime.of(2023, 1, 1, 0, 0)
        val endDate = LocalDateTime.of(2023, 12, 31, 23, 0)
        val totalHours = ChronoUnit.HOURS.between(startDate, endDate) + 1

        // Генерируем 20,000 точек (примерно по 2 точки в час)
        val pointsPerHour = 2
        val totalPoints = 10_000
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


        val asyncTime = measureTime {
            runBlocking {
                buildHierarchyFromFlatData(
                    rawData = rawData,
                    timeResolution = TimeResolution.YEAR,
                    maxPointsPerNode = 50
                )
            }
        }
        println("Асинхронный запрос время $asyncTime")

    }


    @Test
    fun childrenData() {
//        println(ChartData.default<PointNode>().rootNodes[0].children[0].children)
    }

}