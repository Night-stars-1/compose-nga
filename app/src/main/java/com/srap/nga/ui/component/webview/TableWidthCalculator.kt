package com.srap.nga.ui.component.webview

/** Fits compact tables to the viewport while preserving scrolling for genuinely wide tables. */
internal object TableWidthCalculator {
    fun fit(
        naturalWidths: IntArray,
        viewportWidth: Int,
        minimumColumnWidth: Int,
    ): IntArray {
        if (naturalWidths.isEmpty()) return naturalWidths.copyOf()

        val minimum = minimumColumnWidth.coerceAtLeast(1)
        val widths = IntArray(naturalWidths.size) { index ->
            naturalWidths[index].coerceAtLeast(minimum)
        }
        val minimumTotal = minimum.toLong() * widths.size
        if (viewportWidth <= 0 || minimumTotal > viewportWidth) return widths

        val naturalTotal = widths.sumOf { it.toLong() }
        if (naturalTotal == viewportWidth.toLong()) return widths

        if (naturalTotal < viewportWidth) {
            val expansionColumn = widths.indices.maxWithOrNull(
                compareBy<Int> { widths[it] }.thenBy { it }
            ) ?: widths.lastIndex
            widths[expansionColumn] += viewportWidth - naturalTotal.toInt()
            return widths
        }

        val flexibleWidths = LongArray(widths.size) { index ->
            (widths[index] - minimum).toLong()
        }
        val flexibleTotal = flexibleWidths.sum()
        val flexibleBudget = viewportWidth.toLong() - minimumTotal
        if (flexibleTotal <= 0L || flexibleBudget <= 0L) {
            return IntArray(widths.size) { minimum }
        }

        val fitted = IntArray(widths.size) { index ->
            minimum + (flexibleBudget * flexibleWidths[index] / flexibleTotal).toInt()
        }
        var remainder = viewportWidth - fitted.sum()
        val expansionOrder = fitted.indices.sortedWith(
            compareByDescending<Int> { flexibleWidths[it] }.thenByDescending { it }
        )
        var orderIndex = 0
        while (remainder > 0) {
            fitted[expansionOrder[orderIndex % expansionOrder.size]]++
            remainder--
            orderIndex++
        }
        return fitted
    }
}
