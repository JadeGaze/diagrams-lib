package com.example.graphiclib.ui.pieChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.graphiclib.ui.theme.GraphicLibTheme

@Composable
fun PieChart(
    modifier: Modifier,
    charts: List<PieChartModel>,
    style: PieChartStyle,
) {

    Canvas(
        modifier = modifier
    ) {
        var startAngle = 0f
        var sweepAngle = 0f


        var radius = (size.width / 2f)


        val (topLeft, effectiveSize) = if (style.drawStyle is Stroke) {
            val strokeWidth = style.strokeWidth
            Pair(
                Offset(
                    strokeWidth / 2, strokeWidth / 2
                ), Size(
                    width = size.width - strokeWidth,
                    height = size.width - strokeWidth
                )
            )
        } else {
            Pair(Offset.Zero, Size(size.width, size.width))
        }
        charts.forEach {

            sweepAngle = (it.value / 100) * 360

            drawArc(
                color = it.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = style.isPie,
                style = style.drawStyle,
                topLeft = topLeft,
                size = effectiveSize
            )

            startAngle += sweepAngle
        }
    }
}

data class PieChartStyle(
    val strokeWidth: Float,
    val drawStyle: DrawStyle = PieChartDrawStyle.getPieStyle(),
    val isPie: Boolean,
) {
    companion object {
        fun getDefaultPie(strokeWidth: Float) =
            PieChartStyle(strokeWidth = strokeWidth, PieChartDrawStyle.getPieStyle(), isPie = true)

        fun getDefaultDonat(strokeWidth: Float) = PieChartStyle(
            strokeWidth = strokeWidth,
            PieChartDrawStyle.getDonutStyle(strokeWidth = strokeWidth), isPie = false
        )
    }
}

object PieChartDrawStyle {
    fun getDonutStyle(strokeWidth: Float) = Stroke(
        width = strokeWidth,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
    )

    fun getPieStyle() = Fill
}

data class PieChartModel(
    val value: Float,
    val color: Color,
) {
    companion object {
        fun default() = listOf(
            PieChartModel(value = 20f, color = Color.Black),
            PieChartModel(value = 30f, color = Color.Gray),
            PieChartModel(value = 40f, color = Color.Green),
            PieChartModel(value = 10f, color = Color.Red),
        )

        fun default2() = listOf(
            PieChartModel(value = 25f, color = Color.Black),
            PieChartModel(value = 25f, color = Color.Gray),
            PieChartModel(value = 25f, color = Color.Green),
            PieChartModel(value = 25f, color = Color.Red),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    GraphicLibTheme {
        PieChart(
            modifier = Modifier
                .size(300.dp)
                .onGloballyPositioned { coordinates ->
                    println("Real size: ${coordinates.size}")
                }, charts = PieChartModel.default2(),
            style = PieChartStyle.getDefaultPie(with(LocalDensity.current) { 300.dp.toPx() })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DonatChartPreview() {
    GraphicLibTheme {
        PieChart(
            modifier = Modifier.size(300.dp), charts = PieChartModel.default2(),
            style = PieChartStyle.getDefaultDonat(with(LocalDensity.current) { 10.dp.toPx() })
        )
    }
}