package com.example.graphiclib.data

import java.time.LocalDateTime

data class CandleModel(
    val time: LocalDateTime,
    val open: Float,
    val close: Float,
    val high: Float,
    val low: Float,
)