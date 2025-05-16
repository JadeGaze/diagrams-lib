package com.example.graphiclib.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphiclib.data.PointNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BaseViewModel : ViewModel() {

    private val _data = MutableStateFlow<LineChartData?>(null)
    val data: StateFlow<LineChartData?>
        get() = _data

    fun generateLineDataDefaultData() {
        viewModelScope.launch {
            LineChartData.default<PointNode>()
        }
    }

}