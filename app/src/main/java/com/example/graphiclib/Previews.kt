package com.example.graphiclib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.graphiclib.ui.barChart.BarChart
import com.example.graphiclib.ui.barChart.BarChartState
import com.example.graphiclib.ui.barChart.BarChartStyle
import com.example.graphiclib.ui.base.BarChartData
import com.example.graphiclib.ui.theme.GraphicLibTheme

@Preview(showBackground = true)
@Composable
fun BarGraphicScreenDefaultPreview() {
    GraphicLibTheme {
        BarChart(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.white))
                .padding(20.dp)
                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
            chartStyle = BarChartStyle.Default,
            state = BarChartState(data = BarChartData.default())
        )
    }
}

@Preview
@Composable
fun BarGraphicScreenDarkPreview() {
    GraphicLibTheme {
        colorResource(id = R.color.white)
        BarChart(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white))
                .padding(20.dp)
                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
            chartStyle = BarChartStyle.DarkTheme,
            state = BarChartState(data = BarChartData.default())
        )
    }
}

@Preview
@Composable
fun BarGraphicScreenCustomPreview() {
    GraphicLibTheme {
        colorResource(id = R.color.white)
        BarChart(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white))
                .padding(20.dp)
                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
            chartStyle = BarChartStyle(
                axisColor = colorResource(R.color.purple_700),
                barColor = colorResource(R.color.purple_500),
                textColor = colorResource(R.color.black),
            ),
            state = BarChartState(data = BarChartData.default())
        )
    }
}