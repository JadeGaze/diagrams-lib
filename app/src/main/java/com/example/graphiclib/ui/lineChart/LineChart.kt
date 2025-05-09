package com.example.graphiclib.ui.lineChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphiclib.R
import com.example.graphiclib.ui.barChart.BarChartStyle
import com.example.graphiclib.ui.base.LineChartData
import com.example.graphiclib.ui.theme.GraphicLibTheme
import com.example.graphiclib.ui.utils.calculateGridSteps
import com.example.graphiclib.ui.utils.drawAxis
import com.example.graphiclib.ui.utils.drawGridWithSteps

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    chartStyle: BarChartStyle,
    state: LineChartState,
) {

    val textMeasurer = rememberTextMeasurer()
    val (stepSize, steps) = remember(state.maxValue) { calculateGridSteps(state.maxValue) }
    var scale by remember { mutableFloatStateOf(1f) }

    Canvas(
        modifier = modifier
            .padding(40.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale = (scale * zoom)
                    if (scale > 1.5f && state.currentLevel == state.root) {
                        state.zoomIn(state.visibleItems.first())
                    } else if (scale < 0.8f && state.currentLevel != state.root) {
                        state.zoomOut()
                    }
                }
            }
            .scrollable(state = state.scrollableState, orientation = Orientation.Horizontal)
    ) {

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
            ceilNumber = state.visibleItems.size
        )

        drawLines(state, textMeasurer, chartStyle, chartHeight, chartWidth)

    }

}

private fun DrawScope.drawLines(
    state: LineChartState,
    textMeasurer: TextMeasurer,
    chartStyle: BarChartStyle,
    chartHeight: Float,
    chartWidth: Float,
) {
    val segmentWidth = chartWidth / state.visibleItems.size
    val pointWidth = segmentWidth / state.visibleItems.first().values.size

    state.visibleItems.forEachIndexed { index, point ->
        point.values.forEachIndexed { valueIndex, yValue ->
            val xPos = segmentWidth * index + pointWidth * valueIndex
            val yPos = chartHeight - yValue * state.standardUnit

            // Точка
            drawCircle(
                color = chartStyle.barColor,
                radius = 8f,
                center = Offset(xPos, yPos)
            )

            // Соединение с предыдущей точкой
            if (valueIndex > 0) {
                val prevX = segmentWidth * index + pointWidth * (valueIndex - 1)
                val prevY = chartHeight - point.values[valueIndex - 1] * state.standardUnit

                drawLine(
                    color = chartStyle.barColor,
                    start = Offset(prevX, prevY),
                    end = Offset(xPos, yPos),
                    strokeWidth = 3f
                )
            }

            // Соединение с предыдущим узлом
            if (index > 0 && valueIndex == 0) {
                val prevNode = state.visibleItems[index - 1]
                val prevX = segmentWidth * (index - 1) + pointWidth * (prevNode.values.size - 1)
                val prevY = chartHeight - prevNode.values.last() * state.standardUnit

                drawLine(
                    color = chartStyle.barColor,
                    start = Offset(prevX, prevY),
                    end = Offset(xPos, yPos),
                    strokeWidth = 3f
                )
            }
        }
        drawText(
            textMeasurer = textMeasurer,
            text = state.visibleItems.getOrNull(index)?.label ?: "",
            style = TextStyle(
                color = chartStyle.barColor, fontSize = 12.sp, textAlign = TextAlign.Center
            ),
            size = Size(chartWidth / state.visibleItems.size, this.size.height),
            topLeft = Offset(chartWidth / state.visibleItems.size * index, chartHeight + 8)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun LineGraphicScreenDefaultPreview() {
    GraphicLibTheme {
        LineChart(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.white))
                .padding(50.dp)
                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
            chartStyle = BarChartStyle.Default,
            state = LineChartState(
                data = LineChartData.default()
            )
        )
    }
}