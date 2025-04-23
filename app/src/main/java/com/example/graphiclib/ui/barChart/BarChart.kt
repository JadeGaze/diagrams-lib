package com.example.graphiclib.ui.barChart

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    chartStyle: BarChartStyle,
    state: BarChartState,
) {
    val textMeasurer = rememberTextMeasurer()
    var selectedRectIndex by remember { mutableIntStateOf(-1) }

    val averageLabel = "average ${state.avgValue}"

    val text by remember { derivedStateOf { "${if (selectedRectIndex == -1) state.sumValue else state.visibleBars[selectedRectIndex].value} чего-то" } }
    val (stepSize, steps) = remember(state.maxValue) { calculateGridSteps(state.maxValue) }

    var scale by remember { mutableFloatStateOf(1f) }
    var currentLevel by remember { mutableStateOf(state.currentLevel) }



    Column(modifier = modifier.background(Color.White)) {
        Text(text)
        Canvas(
            modifier = modifier
                .padding(vertical = 40.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scale = (scale * zoom)
                        if (scale > 1.5f && state.currentLevel == state.root) {
                            state.zoomIn(state.visibleBars.first())
                            currentLevel = state.currentLevel
                        } else if (scale < 0.8f && state.currentLevel != state.root) {
                            state.zoomOut()
                            currentLevel = state.currentLevel
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset ->
                        val currSelected = offset.isBar(state.clickableRectangles)
                        selectedRectIndex =
                            if (currSelected == selectedRectIndex) -1 else currSelected
                    })
                }) {

            val chartWidth = size.width - 128.dp.value
            val chartHeight = size.height - 64.dp.value

            state.setChartSize(chartWidth, chartHeight)

            drawAxis(
                chartWidth = chartWidth,
                chartHeight = chartHeight,
                axisColor = chartStyle.axisColor,
            )

            drawGridWithSteps(
                steps = steps,
                stepSize = stepSize,
                maxValue = state.maxValue,
                style = chartStyle,
                textMeasurer = textMeasurer,
                chartHeight = chartHeight,
                chartWidth = chartWidth,
                standardUnit = state.standardUnit,
                ceilNumber = state.visibleBars.size
            )

            drawBars(
                state = state,
                textMeasurer = textMeasurer,
                chartStyle = chartStyle,
                selectedRectIndex = selectedRectIndex,
                chartHeight = chartHeight,
                chartWidth = chartWidth
            )

            drawAvg(
                chartStyle,
                chartHeight - state.avgValue * state.standardUnit,
                averageLabel,
                textMeasurer,
                chartWidth
            )
        }
    }
}

fun DrawScope.drawBars(
    state: BarChartState,
    textMeasurer: TextMeasurer,
    chartStyle: BarChartStyle,
    selectedRectIndex: Int,
    chartHeight: Float,
    chartWidth: Float,
) {
    state.visibleBars.forEachIndexed { index, bar ->
        val xOffset = state.xOffset(bar) + state.space
        val yOffset = chartHeight - bar.value * state.standardUnit

        drawRect(
            color = if (index == selectedRectIndex) chartStyle.barColor
            else chartStyle.barColor.copy(alpha = if (selectedRectIndex == -1) 1f else 0.5f),
            topLeft = Offset(xOffset, yOffset),
            size = Size(state.barWidth, bar.value * state.standardUnit)
        )

        drawText(
            textMeasurer = textMeasurer,
            text = state.visibleBars.getOrNull(index)?.label ?: "",
            style = TextStyle(
                color = if (index == selectedRectIndex) {
                    chartStyle.barColor
                } else {
                    if (selectedRectIndex == -1) chartStyle.barColor else chartStyle.barColor.copy(
                        alpha = 0.5f
                    )
                }, fontSize = 12.sp, textAlign = TextAlign.Center
            ),
            size = Size(chartWidth / state.visibleBars.size, this.size.height),
            topLeft = Offset(chartWidth / state.visibleBars.size * index, chartHeight + 8)
        )
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


private fun DrawScope.drawAxis(
    chartWidth: Float,
    chartHeight: Float,
    axisColor: Color,
    paddingStart: Float = 0.dp.value,
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
        }, color = axisColor
    )

    // Y ->
    drawPath(
        path = Path().apply {
            moveTo(chartWidth + paddingStart, chartHeight)
            lineTo(chartWidth + paddingStart - 10.dp.toPx(), chartHeight - 5.dp.toPx())
            lineTo(chartWidth + paddingStart - 10.dp.toPx(), chartHeight + 5.dp.toPx())
            close()
        }, color = axisColor
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

    drawText(
        textMeasurer = textMeasurer, text = averageLabel, style = TextStyle(
            color = chartStyle.barColor, fontSize = 10.sp, textAlign = TextAlign.Center
        ), topLeft = Offset(chartWidth + 20, averageValue - 20)
    )
}

private fun DrawScope.drawGridWithSteps(
    steps: Int,
    stepSize: Float,
    maxValue: Float,
    style: BarChartStyle,
    textMeasurer: TextMeasurer,
    chartHeight: Float,
    chartWidth: Float,
    standardUnit: Float,
    ceilNumber: Int,
) {

    // Horizontal grid lines
    for (i in 0..<steps) {
        val yValue = i * stepSize
        val yPos = ((chartHeight - (yValue / maxValue * chartHeight)) * 10).roundToInt() / 10f

        drawLine(
            color = style.barColor,
            strokeWidth = style.gridStrokeWidth.value,
            start = Offset(0f, yPos),
            end = Offset(chartWidth, yPos),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(10f, 10f), phase = 5f
            )
        )

        Log.d("STATE", "${(chartHeight - yPos)}")

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

    val vertStep = chartWidth / ceilNumber
    for (i in 1..ceilNumber) {
        val xPos = vertStep * i
        drawLine(
            color = style.barColor,
            strokeWidth = 2.dp.value,
            start = Offset(xPos, 0f),
            end = Offset(xPos, chartHeight),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(10f, 10f),
                phase = 5f
            )
        )
    }
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