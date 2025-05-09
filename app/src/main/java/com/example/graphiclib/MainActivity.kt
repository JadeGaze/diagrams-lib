package com.example.graphiclib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.graphiclib.ui.base.LineChartData
import com.example.graphiclib.ui.lineChart.LineChart
import com.example.graphiclib.ui.lineChart.LineChartState
import com.example.graphiclib.ui.theme.GraphicLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GraphicLibTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
//                        BarChart(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(500.dp)
//                                .background(colorResource(id = R.color.white))
//                                .padding(innerPadding)
//                                .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
//                            chartStyle = BarChartStyle.Default,
//                            state = BarChartState(BarChartData.generateLineSampleData())
//                        )
//                        Spacer(Modifier.height(15.dp))
                        LineChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                                .background(colorResource(id = R.color.white))
                                .padding(innerPadding),
                            chartStyle = BarChartStyle.Default,
                            state = LineChartState(LineChartData.default())
                        )
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    GraphicLibTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                BarChart(
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .background(colorResource(id = R.color.white))
                        .padding(innerPadding)
                        .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
                    chartStyle = BarChartStyle.Default,
                    state = BarChartState(BarChartData.default())
                )
                Spacer(Modifier.height(100.dp))
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .background(colorResource(id = R.color.white))
                        .padding(innerPadding),
                    chartStyle = BarChartStyle.Default,
                    state = LineChartState(LineChartData.default())
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GraphicLibTheme {
        Greeting("Android")
    }
}