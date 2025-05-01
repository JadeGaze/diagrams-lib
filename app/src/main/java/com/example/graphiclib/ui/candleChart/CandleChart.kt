package com.example.graphiclib.ui.candleChart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.graphiclib.ui.barChart.drawAxis

@Composable
fun CandleChart(
    modifier: Modifier,
    chartStyle: CandleChartStyle,
    state: CandleChartState,
) {

    Canvas(modifier) {
        val chartWidth = size.width - 128.dp.value
        val chartHeight = size.height - 64.dp.value

        state.setChartSize(chartWidth, chartHeight)

        drawAxis(
            chartWidth,
            chartHeight,
            axisColor = chartStyle.axisColor,
        )


    }

}