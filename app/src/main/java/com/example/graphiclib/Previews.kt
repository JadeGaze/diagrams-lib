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
import com.example.graphiclib.ui.BarChart
import com.example.graphiclib.ui.BarChartStyle
import com.example.graphiclib.ui.ProductivityModel
import com.example.graphiclib.ui.theme.GraphicLibTheme

@Preview (showBackground = true)
@Composable
fun GraphicScreenDefaultPreview() {
    GraphicLibTheme {
        BarChart(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.white))
                .padding(20.dp)
                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
            chartStyle = BarChartStyle.Default,
            data = listOf(
                ProductivityModel(id = "", result = 8f),
                ProductivityModel(id = "", result = 15f),
                ProductivityModel(id = "", result = 2f),
                ProductivityModel(id = "", result = 4f),
                ProductivityModel(id = "", result = 6f),
                ProductivityModel(id = "", result = 10f),
                ProductivityModel(id = "", result = 5f),
                ProductivityModel(id = "", result = 1f),
            )
        )
    }
}

@Preview
@Composable
fun GraphicScreenDarkPreview() {
    GraphicLibTheme {
        colorResource(id = R.color.white)
        BarChart(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white))
                .padding(20.dp)
                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
            chartStyle = BarChartStyle.DarkTheme,
            data = listOf(
                ProductivityModel(id = "", result = 8f),
                ProductivityModel(id = "", result = 2f),
                ProductivityModel(id = "", result = 4f),
                ProductivityModel(id = "", result = 6f),
                ProductivityModel(id = "", result = 10f),
                ProductivityModel(id = "", result = 5f),
                ProductivityModel(id = "", result = 1f),
            )
        )
    }
}

@Preview
@Composable
fun GraphicScreenCustomPreview() {
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
            data = listOf(
                ProductivityModel(id = "", result = 8f),
                ProductivityModel(id = "", result = 2f),
                ProductivityModel(id = "", result = 4f),
                ProductivityModel(id = "", result = 6f),
                ProductivityModel(id = "", result = 10f),
                ProductivityModel(id = "", result = 5f),
                ProductivityModel(id = "", result = 1f),
            )
        )
    }
}