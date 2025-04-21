package com.example.graphiclib.ui

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphiclib.data.BarChartData
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt


private data class ZoomState(
    val onZoom: (zoomChange: Float, panChange: Offset) -> Unit = { _, _ -> },
    val onZoomEnd: (() -> Unit)? = null,
)

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    chartStyle: BarChartStyle,
    data: List<ProductivityModel> = emptyList(),
) {

    val daysOfWeek = listOf(
        "пн",
        "вт",
        "ср",
        "чт",
        "пт",
        "сб",
        "вс",
    )

    val textMeasurer = rememberTextMeasurer()
    val gridPaint = remember(chartStyle.gridColor) {
        Paint().apply {
            color = chartStyle.gridColor
            strokeWidth = chartStyle.gridStrokeWidth.value
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
        }
    }
    val rectangles = mutableListOf<BarRectangle>()
    var selectedRectIndex by remember { mutableIntStateOf(-1) }
//    val currentVal = data.getOrNull(selectedRectIndex)
    var sumValues = 0f
//    data.forEach { sumValues += it.result }
//    val text = "${currentVal?.result ?: sumValues} чего-то"
    var averageLabel = "average"

    var averageValue by remember { mutableFloatStateOf(0f) }
    var chartSize by remember { mutableStateOf(Size.Zero) }

//    val maxValue = remember { data.maxByOrNull { it.result }?.result } ?: 0f
    var standardUnit by remember { mutableFloatStateOf(0f) }

//    val (stepSize, steps) = remember(maxValue) { calculateGridSteps(maxValue) }

//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    var currentData by remember { mutableStateOf(BarChartData.generateSampleData()) }
//    var scale by remember { mutableFloatStateOf(1f) }
    var maxScale by remember { mutableFloatStateOf(1f) }
    val currentVal = currentData.currentLevel.getOrNull(selectedRectIndex)
    val text = "${currentVal?.value ?: sumValues} чего-то"
    currentData.currentLevel.forEach { sumValues += it.value }
    val maxValue = remember { currentData.currentLevel.maxByOrNull { it.value }?.value } ?: 0f
    val (stepSize, steps) = remember(maxValue) { calculateGridSteps(maxValue) }
    LaunchedEffect(currentData.currentLevel.size) {
        maxScale = currentData.currentLevel.size.coerceAtLeast(1).toFloat()
    }


    // Текущий уровень масштабирования (1.0 = 100%)
    var scale by remember { mutableStateOf(1f) }
    // Смещение графика
    var offset by remember { mutableStateOf(Offset.Zero) }
    // Текущий уровень детализации
    var currentLevel by remember { mutableStateOf(currentData.rootNodes) }

    // Анимация изменений
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(800),
        label = "scale_animation"
    )

//    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    LaunchedEffect(currentData) {

    }

    LaunchedEffect(chartSize) {

        if (chartSize != Size.Zero && data.isNotEmpty()) {
            standardUnit = (chartSize.height / maxValue)
        }
        averageValue = sumValues / data.size * standardUnit
        averageLabel = "$averageValue"
    }

    Column(modifier = modifier.background(Color.White)) {


        Text(text)


        Canvas(
            modifier = modifier
                .padding(vertical = 40.dp)
                .pointerInput(Unit) {

                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom)
                        val maxOffset = calculateMaxOffset(newScale)

                        scale = newScale
                        if (scale > 1.5f && currentLevel == currentData.rootNodes) {
                            currentData.zoomIn(currentData.rootNodes.first())
                            currentLevel = currentData.currentLevel
                        } else if (scale < 0.8f && currentLevel != currentData.rootNodes) {
                            currentData.zoomOut()
                            currentLevel = currentData.currentLevel
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset ->
                        val currSelected = offset.isBar(rectangles)
                        selectedRectIndex =
                            if (currSelected == selectedRectIndex) -1 else offset.isBar(rectangles)
                    })
                }) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val chartWidth = canvasWidth - 128.dp.value
            val chartHeight = canvasHeight - 64.dp.value

            chartSize = Size(width = chartWidth, height = chartHeight)

            val cellNumber = 4
            val ceilHeight = chartHeight / cellNumber
            val ceilWidth = floor(chartWidth / 7)

            val avg =
                mutableFloatStateOf((sumValues / data.size * standardUnit))

            drawGridWithSteps(
                paint = gridPaint,
                steps = steps,
                stepSize = stepSize,
                maxValue = maxValue,
                style = chartStyle,
                textMeasurer = textMeasurer,
                chartHeight = chartHeight,
                chartWidth = chartWidth,
                standardUnit = standardUnit
            )

            for (i in 0..6) {
                val x = ceilWidth * i + ceilWidth
                drawLine(
                    color = chartStyle.barColor,
                    strokeWidth = 2.dp.value,
                    start = Offset(x, 0f),
                    end = Offset(x, chartHeight),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(10f, 10f),
                        phase = 5f
                    )
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = currentLevel.getOrNull(i)?.label ?: "",
                    style = TextStyle(
                        color = if (i == selectedRectIndex) {
                            chartStyle.barColor
                        } else {
                            if (selectedRectIndex == -1) chartStyle.barColor else chartStyle.barColor.copy(
                                alpha = 0.5f
                            )
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    ),
                    size = Size(ceilWidth, this.size.height),
                    topLeft = Offset(ceilWidth * i, chartHeight + 8)
                )
            }

            val dayWidth = ceilWidth / 2
            val space = dayWidth / 2
            currentData.currentLevel.forEachIndexed { index, day ->
                val dayHeight = day.value * standardUnit

                val x = index * ceilWidth + space
                val y = chartHeight - dayHeight

                rectangles.add(
                    BarRectangle(
                        index,
                        Offset(x, y),
                        Offset(x + dayWidth, chartHeight),
                        chartStyle.barColor
                    )
                )

                drawRect(
                    color = if (index == selectedRectIndex) {
                        chartStyle.barColor
                    } else {
                        if (selectedRectIndex == -1) chartStyle.barColor else chartStyle.barColor.copy(
                            alpha = 0.5f
                        )
                    },
                    topLeft = Offset(x, y),
                    size = Size(dayWidth, dayHeight)
                )
            }

            drawAvg(chartStyle, chartHeight - averageValue, averageLabel, textMeasurer, chartWidth)


            drawAxis(
                chartWidth = chartWidth,
                chartHeight = chartHeight,
                axisColor = chartStyle.axisColor,
            )

        }
    }
}

private fun Offset.isBar(bars: MutableList<BarRectangle>): Int {
    bars.forEachIndexed { index, bar ->
        if (bar.contains(this)) {
            return index
        }
    }
    return -1
}


data class BarRectangle(
    val id: Int,
    val topLeft: Offset,
    val bottomRight: Offset,
    var color: Color,
) {
    fun contains(point: Offset): Boolean =
        point.x in topLeft.x..bottomRight.x && point.y in topLeft.y..bottomRight.y
}


private fun DrawScope.drawAxis(
    chartWidth: Float,
    chartHeight: Float,
    axisColor: Color,
    paddingStart: Float = 0.dp.value,
    paddingBottom: Float = 0.dp.value,
) {
    // X
    drawLine(
        color = axisColor,
        strokeWidth = 4.dp.value,
        start = Offset(paddingStart, chartHeight),
        end = Offset(chartWidth + paddingStart, chartHeight),
    )

    // Y
    drawLine(
        color = axisColor,
        strokeWidth = 4.dp.value,
        start = Offset(paddingStart, 0f),
        end = Offset(paddingStart, chartHeight),
    )

    // X ->
    drawPath(
        path = Path().apply {
            moveTo(paddingStart, 0f)
            lineTo(paddingStart - 5.dp.toPx(), 10.dp.toPx())
            lineTo(paddingStart + 5.dp.toPx(), 10.dp.toPx())
            close()
        },
        color = axisColor
    )

    // Y ->
    drawPath(
        path = Path().apply {
            moveTo(chartWidth + paddingStart, chartHeight)
            lineTo(chartWidth + paddingStart - 10.dp.toPx(), chartHeight - 5.dp.toPx())
            lineTo(chartWidth + paddingStart - 10.dp.toPx(), chartHeight + 5.dp.toPx())
            close()
        },
        color = axisColor
    )
}

private fun DrawScope.drawAvg(
    chartStyle: BarChartStyle,
    averageValue: Float,
    averageLabel: String,
    textMeasurer: TextMeasurer,
    chartWidth: Float,
) {
    drawLine(
        color = chartStyle.barColor,
        strokeWidth = 4.dp.value,
        start = Offset(0f, averageValue),
        end = Offset(chartWidth, averageValue),
    )

//    Log.d("AVG", "averageValue: $averageValue")

    drawText(
        textMeasurer = textMeasurer,
        text = averageLabel,
        style = TextStyle(
            color = chartStyle.barColor,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        ),
        topLeft = Offset(chartWidth + 20, averageValue - 20)
    )
}

private fun DrawScope.drawGridWithSteps(
    paint: Paint,
    steps: Int,
    stepSize: Float,
    maxValue: Float,
    style: BarChartStyle,
    textMeasurer: TextMeasurer,
    chartHeight: Float,
    chartWidth: Float,
    standardUnit: Float,
) {


    // Horizontal grid lines
    for (i in 0..steps) {
        val yValue = i * stepSize
        val yPos = ((chartHeight - (yValue / maxValue * chartHeight)) * 10).roundToInt() / 10f

        drawLine(
            color = style.barColor,
            strokeWidth = style.gridStrokeWidth.value,
            start = Offset(0f, yPos),
            end = Offset(chartWidth, yPos),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(10f, 10f),
                phase = 5f
            )
        )

//        Log.d("STEP", "$yPos")

        drawText(
            textMeasurer = textMeasurer,
            text = String.format("%.1f", (chartHeight - yPos) / standardUnit),
            style = TextStyle(
                color = style.barColor,
                fontSize = 12.sp,
            ),
            topLeft = Offset(-32.dp.toPx(), yPos - 20.dp.value)
        )
    }

    //            for (i in 1..4) {
//                val y = chartHeight - ceilHeight * i
//                drawLine(
//                    color = chartStyle.barColor,
//                    strokeWidth = 2.dp.value,
//                    start = Offset(0f, y),
//                    end = Offset(chartWidth, y),
//                    pathEffect = PathEffect.dashPathEffect(
//                        intervals = floatArrayOf(10f, 10f),
//                        phase = 5f
//                    )
//                )
//
//                drawText(
//                    textMeasurer = textMeasurer,
//                    text = String.format("%.1f", (chartHeight - y) / standardUnit),
//                    style = TextStyle(
//                        color = chartStyle.barColor,
//                        fontSize = 12.sp,
//                    ),
//                    topLeft = Offset(-32.dp.toPx(), y - 20.dp.value)
//                )
//            }
}

private fun calculateGridSteps(maxValue: Float): Pair<Float, Int> {

    val exponent = floor(log10(maxValue)).toInt()
    val fraction = maxValue / 10f.pow(exponent)

    val stepSize = when {
        fraction <= 1.2 -> 0.25f
        fraction <= 1.5 -> 0.3f
        fraction <= 2 -> 0.4f
        fraction <= 3 -> 0.5f
        fraction <= 7 -> 1f
        else -> 2f
    } * 10f.pow(exponent)

    val adjustedStep = adjustStepSize(stepSize, maxValue)
    val steps = ceil(maxValue / adjustedStep).toInt()

    return adjustedStep to steps
}

private fun adjustStepSize(step: Float, maxValue: Float): Float {
    var adjusted = step
    while (maxValue / adjusted > 8) adjusted *= 2
    while (maxValue / adjusted < 4) adjusted /= 2
    return adjusted
}

private fun calculateMaxOffset(scale: Float): Offset {
    return Offset(
        x = (scale - 1) * 100f, // Макс. смещение по X
        y = (scale - 1) * 50f   // Макс. смещение по Y
    )
}