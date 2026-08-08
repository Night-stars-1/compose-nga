package com.srap.nga.utils.nga.handler

import org.junit.Assert.assertEquals
import org.junit.Test

class BBCodeHandlerTest {
    @Test
    fun `maps NGA author colors to readable dark theme colors`() {
        assertEquals(
            0xFFF2B8B5.toInt(),
            BBCodeHandler.darkThemeColor(0xFFD32F2F.toInt()),
        )
        assertEquals(
            0xFFCAC4D0.toInt(),
            BBCodeHandler.darkThemeColor(0xFF757575.toInt()),
        )
    }

    @Test
    fun `keeps unknown author colors unchanged`() {
        assertEquals(
            0xFF123456.toInt(),
            BBCodeHandler.darkThemeColor(0xFF123456.toInt()),
        )
    }
}
