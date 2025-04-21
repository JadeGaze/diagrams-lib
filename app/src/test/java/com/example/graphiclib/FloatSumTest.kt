package com.example.graphiclib

import com.example.graphiclib.ui.ProductivityModel
import org.junit.Assert.assertEquals
import org.junit.Test

class FloatSumTest {
    private val data = listOf(
        ProductivityModel(id = "", result = 8f),
        ProductivityModel(id = "", result = 2f),
        ProductivityModel(id = "", result = 4f),
        ProductivityModel(id = "", result = 6f),
        ProductivityModel(id = "", result = 10f),
        ProductivityModel(id = "", result = 5f),
        ProductivityModel(id = "", result = 1f),
    )

    private var sumValues = 0f

    @Test
    fun floatSumTest_isSuccess() {


        data.forEach { sumValues += it.result }

        assertEquals(sumValues, 36f)
    }

    @Test
    fun averageTest_isSuccess() {
        data.forEach { sumValues += it.result }
        val averageValue = sumValues / data.size
        println(averageValue)
    }

}