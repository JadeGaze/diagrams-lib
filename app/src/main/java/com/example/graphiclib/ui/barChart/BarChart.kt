package com.example.graphiclib.ui.barChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphiclib.ui.utils.calculateGridSteps
import com.example.graphiclib.ui.utils.drawAvg
import com.example.graphiclib.ui.utils.drawAxis
import com.example.graphiclib.ui.utils.drawGridWithSteps

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    chartStyle: BarChartStyle,
    state: BarChartState,
) {
    val textMeasurer = rememberTextMeasurer()
    var selectedRectIndex by remember { mutableIntStateOf(-1) }

    val averageLabel = "average ${state.avgValue}"

    val text by remember { derivedStateOf { "${if (selectedRectIndex == -1) state.sumValue else state.visibleItems[selectedRectIndex].value} чего-то" } }
    val (stepSize, steps) = remember(state.maxValue) { calculateGridSteps(state.maxValue) }

    var scale by remember { mutableFloatStateOf(1f) }
    var currentLevel by remember { mutableStateOf(state.currentLevel) }



    Column(modifier = modifier.background(Color.White)) {
        Text(text)
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scale = (scale * zoom)
                        if (scale > 1.5f && state.currentLevel == state.root) {
                            state.zoomIn(state.visibleItems.first())
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
                }
                .scrollable(state = state.scrollableState, Orientation.Horizontal)

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
    state.visibleItems.forEachIndexed { index, bar ->
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
            text = state.visibleItems.getOrNull(index)?.label ?: "",
            style = TextStyle(
                color = if (index == selectedRectIndex) {
                    chartStyle.barColor
                } else {
                    if (selectedRectIndex == -1) chartStyle.barColor else chartStyle.barColor.copy(
                        alpha = 0.5f
                    )
                }, fontSize = 12.sp, textAlign = TextAlign.Center
            ),
            size = Size(chartWidth / state.visibleItems.size, this.size.height),
            topLeft = Offset(chartWidth / state.visibleItems.size * index, chartHeight + 8)
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