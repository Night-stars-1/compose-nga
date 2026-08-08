package com.srap.nga.ui.component.webview

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class TableWidthCalculatorTest {
    @Test
    fun `compact table shrinks to the viewport and keeps useful proportions`() {
        val widths = TableWidthCalculator.fit(
            naturalWidths = intArrayOf(150, 500),
            viewportWidth = 400,
            minimumColumnWidth = 100,
        )

        assertArrayEquals(intArrayOf(122, 278), widths)
        assertEquals(400, widths.sum())
    }

    @Test
    fun `compact table expands its widest column to fill the viewport`() {
        val widths = TableWidthCalculator.fit(
            naturalWidths = intArrayOf(130, 160),
            viewportWidth = 400,
            minimumColumnWidth = 100,
        )

        assertArrayEquals(intArrayOf(130, 270), widths)
    }

    @Test
    fun `genuinely wide table keeps minimum tracks for horizontal scrolling`() {
        val widths = TableWidthCalculator.fit(
            naturalWidths = IntArray(15) { 20 },
            viewportWidth = 400,
            minimumColumnWidth = 48,
        )

        assertEquals(720, widths.sum())
        widths.forEach { assertEquals(48, it) }
    }
}
