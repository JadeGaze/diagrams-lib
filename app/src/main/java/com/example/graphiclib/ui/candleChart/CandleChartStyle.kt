package com.example.graphiclib.ui.candleChart

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.graphiclib.ui.barChart.BarChartStyle

@Immutable
data class CandleChartStyle(
    val axisColor: Color,
    val barColor: Color,
    val gridColor: Color = axisColor.copy(alpha = 0.3f),
    val textColor: Color,

    // Текст
    val chartLabelStyle: TextStyle = TextStyle.Default,
    val axisLabelStyle: TextStyle = TextStyle.Default,
    val axisValuesStyle: TextStyle = TextStyle.Default,

    // Толщины
    val axisStrokeWidth: Dp = 2.dp,
    val gridStrokeWidth: Dp = 2.dp,

    // Отступы
    val padding: PaddingValues = PaddingValues(16.dp),

    // Анимация
    val animationSpec: AnimationSpec<Float> = tween(300),

    // Эффекты
    val barCornerRadius: Dp = 4.dp,
    val barElevation: Dp = 2.dp,
) {
    companion object {
        val Default = BarChartStyle(
            axisColor = Color.Black,
            barColor = Color.Blue,
            textColor = Color.DarkGray
        )
        val DarkTheme = BarChartStyle(
            axisColor = Color.White,
            barColor = Color.Cyan,
            gridColor = Color.LightGray.copy(alpha = 0.2f),
            textColor = Color.White
        )
    }
}