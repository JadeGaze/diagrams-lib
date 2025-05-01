package com.example.graphiclib.ui.base

open class BaseState {
    protected var chartWidth = 0f
    protected var chartHeight = 0f

    fun setChartSize(width: Float, height: Float) {
        chartWidth = width
        chartHeight = height
    }
}
