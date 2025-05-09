package com.example.graphiclib.ui.utils

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphiclib.ui.barChart.BarChartStyle
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

fun DrawScope.drawAxis(
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

fun DrawScope.drawAvg(
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

fun DrawScope.drawGridWithSteps(
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
        Log.d("STATE", "${(chartHeight - yPos)}")
        Log.d("STATE TEXT", "${(chartHeight - yPos) / standardUnit}")

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

    // vertical grid lines
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

fun calculateGridSteps(maxValue: Float): Pair<Float, Int> {

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

fun adjustStepSize(step: Float, maxValue: Float): Float {
    var adjusted = step
    while (maxValue / adjusted > 8) adjusted *= 2
    while (maxValue / adjusted < 4) adjusted /= 2
    return adjusted
}