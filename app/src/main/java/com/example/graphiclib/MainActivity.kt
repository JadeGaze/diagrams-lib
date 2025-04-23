package com.example.graphiclib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.graphiclib.data.BarChartData
import com.example.graphiclib.ui.barChart.BarChart
import com.example.graphiclib.ui.barChart.BarChartStyle
import com.example.graphiclib.ui.ProductivityModel
import com.example.graphiclib.ui.barChart.BarChartState
import com.example.graphiclib.ui.theme.GraphicLibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GraphicLibTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BarChart(
                        Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.white))
                            .padding(innerPadding)
                            .height((LocalConfiguration.current.screenWidthDp * 9 / 16).dp),
                        chartStyle = BarChartStyle.Default,
                        state = BarChartState(BarChartData.generateSampleData())
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GraphicLibTheme {
        Greeting("Android")
    }
}