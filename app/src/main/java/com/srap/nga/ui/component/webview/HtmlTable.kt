package com.srap.nga.ui.component.webview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.text.LineBreaker
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Layout
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.ViewCompat
import com.srap.nga.utils.nga.handler.BBCodeHandler
import com.srap.nga.utils.nga.handler.CoilImageGetter
import com.srap.nga.utils.nga.parse.NgaTable
import com.srap.nga.utils.nga.parse.NgaTableCell
import com.srap.nga.utils.nga.parse.NgaTableCellAlignment
import kotlin.math.max

private data class TableAppearance(
    val text: Int,
    val link: Int,
    val background: Int,
    val headerBackground: Int,
    val divider: Int,
    val bodyTextSizeSp: Float,
)

private class TableViewHolder(
    val scrollView: NgaTableHorizontalScrollView,
    val container: LinearLayout,
) {
    var table: NgaTable? = null
    var appearance: TableAppearance? = null
}

@Composable
fun HtmlTable(
    table: NgaTable,
    modifier: Modifier = Modifier,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
    onViewPostByPid: ((Int) -> Unit)? = null,
) {
    val currentOnViewPost by rememberUpdatedState(onViewPost)
    val currentOpenUrl by rememberUpdatedState(openUrl)
    val currentOnViewPostByPid by rememberUpdatedState(onViewPostByPid)
    val containerColor = MaterialTheme.colorScheme.surface
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val appearance = TableAppearance(
        text = MaterialTheme.colorScheme.onSurface.toArgb(),
        link = MaterialTheme.colorScheme.primary.toArgb(),
        background = containerColor.toArgb(),
        headerBackground = MaterialTheme.colorScheme.surfaceContainerHigh.toArgb(),
        divider = dividerColor.toArgb(),
        bodyTextSizeSp = MaterialTheme.typography.bodyMedium.fontSize.value,
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(1.dp, dividerColor),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                val container = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                }
                NgaTableHorizontalScrollView(context).apply {
                    isFillViewport = true
                    isHorizontalScrollBarEnabled = true
                    overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
                    addView(container)
                    tag = TableViewHolder(this, container)
                }
            },
            update = { scrollView ->
                val holder = scrollView.tag as TableViewHolder
                if (holder.table != table || holder.appearance != appearance) {
                    holder.table = table
                    holder.appearance = appearance
                    holder.renderTable(
                        table = table,
                        appearance = appearance,
                        onViewPost = { currentOnViewPost(it) },
                        openUrl = { currentOpenUrl(it) },
                        onViewPostByPid = currentOnViewPostByPid,
                    )
                }
            },
        )
    }
}

private fun TableViewHolder.renderTable(
    table: NgaTable,
    appearance: TableAppearance,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
    onViewPostByPid: ((Int) -> Unit)?,
) {
    scrollView.grid = null
    container.removeAllViews()

    if (table.leadingHtml.isNotBlank()) {
        container.addView(
            container.createHtmlTextView(
                html = table.leadingHtml,
                appearance = appearance,
                onViewPost = onViewPost,
                openUrl = openUrl,
                onViewPostByPid = onViewPostByPid,
                fitImagesToWidth = true,
            ).apply {
                setPadding(dp(12), dp(10), dp(12), dp(10))
            }
        )
    }

    val grid = NgaTableGridLayout(container.context).apply {
        configure(
            rowCount = table.rowCount,
            columnCount = table.columnCount,
            minimumColumnWidth = dp(48),
            dividerColor = appearance.divider,
            dividerWidth = dp(1).toFloat(),
        )
    }
    table.cells.forEach { cell ->
        val isHeader = cell.isHeader
        val cellBackground = ColorDrawable(
            if (isHeader) appearance.headerBackground else appearance.background
        )
        if (cell.html.isBlank()) {
            grid.addView(
                View(container.context).apply {
                    background = cellBackground
                },
                cell.tableLayoutParams(),
            )
        } else {
            val textView = container.createHtmlTextView(
                html = cell.html,
                appearance = appearance,
                onViewPost = onViewPost,
                openUrl = openUrl,
                onViewPostByPid = onViewPostByPid,
                fitImagesToWidth = true,
            ).apply {
                gravity = cell.alignment.toGravity() or Gravity.CENTER_VERTICAL
                minWidth = dp(48 * cell.columnSpan)
                minHeight = dp(48 * cell.rowSpan)
                maxWidth = dp(280 * cell.columnSpan)
                setPadding(dp(12), dp(8), dp(12), dp(8))
                background = cellBackground
                if (isHeader) {
                    setTypeface(typeface, Typeface.BOLD)
                    ViewCompat.setAccessibilityHeading(this, true)
                }
            }
            grid.addView(textView, cell.tableLayoutParams())
        }
    }
    scrollView.grid = grid
    container.addView(grid)
}

private class NgaTableHorizontalScrollView(context: Context) : HorizontalScrollView(context) {
    var grid: NgaTableGridLayout? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewportWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            0
        } else {
            (MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight).coerceAtLeast(0)
        }
        grid?.viewportWidth = viewportWidth
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

/**
 * Measures table tracks explicitly so rowspan and colspan remain deterministic. Compact tables
 * are fitted to the viewport; tables whose minimum tracks cannot fit remain horizontally scrollable.
 */
private class NgaTableGridLayout(context: Context) : ViewGroup(context) {
    private var configuredRowCount = 0
    private var configuredColumnCount = 0
    private var minimumColumnWidth = 1
    private var dividerColor = 0
    private var dividerWidth = 1f
    private var columnWidths = IntArray(0)
    private var rowHeights = IntArray(0)
    var viewportWidth: Int = 0

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    fun configure(
        rowCount: Int,
        columnCount: Int,
        minimumColumnWidth: Int,
        dividerColor: Int,
        dividerWidth: Float,
    ) {
        val newRowCount = rowCount.coerceAtLeast(0)
        val newColumnCount = columnCount.coerceAtLeast(0)
        if (configuredRowCount != newRowCount || configuredColumnCount != newColumnCount) {
            configuredRowCount = newRowCount
            configuredColumnCount = newColumnCount
            columnWidths = IntArray(configuredColumnCount)
            rowHeights = IntArray(configuredRowCount)
        }
        this.minimumColumnWidth = minimumColumnWidth.coerceAtLeast(1)
        this.dividerColor = dividerColor
        this.dividerWidth = dividerWidth.coerceAtLeast(1f)
        requestLayout()
        invalidate()
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        TableCellLayoutParams(row = 0, column = 0, rowSpan = 1, columnSpan = 1)

    override fun checkLayoutParams(layoutParams: LayoutParams?): Boolean =
        layoutParams is TableCellLayoutParams

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (configuredRowCount == 0 || configuredColumnCount == 0 || childCount == 0) {
            columnWidths.fill(0)
            rowHeights.fill(0)
            setMeasuredDimension(
                resolveSize(suggestedMinimumWidth, widthMeasureSpec),
                resolveSize(suggestedMinimumHeight, heightMeasureSpec),
            )
            return
        }

        columnWidths.fill(minimumColumnWidth)
        rowHeights.fill(minimumColumnWidth)
        val unspecified = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        // Single-column cells establish the natural base width for each track.
        forEachCell { child, params ->
            child.measure(unspecified, unspecified)
            if (params.columnSpan == 1 && params.column in columnWidths.indices) {
                columnWidths[params.column] = max(
                    columnWidths[params.column],
                    child.measuredWidth,
                )
            }
        }

        // Multi-column cells only grow the columns they cover.
        forEachCell { child, params ->
            if (params.columnSpan > 1) {
                distributeDeficit(
                    tracks = columnWidths,
                    start = params.column,
                    span = params.columnSpan,
                    requiredSize = child.measuredWidth,
                )
            }
        }

        columnWidths = TableWidthCalculator.fit(
            naturalWidths = columnWidths,
            viewportWidth = viewportWidth,
            minimumColumnWidth = minimumColumnWidth,
        )

        // Text and inline image height depends on the final cell width.
        forEachCell { child, params ->
            val cellWidth = columnWidths.sumRange(params.column, params.columnSpan)
            child.measure(
                MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY),
                unspecified,
            )
            if (params.rowSpan == 1 && params.row in rowHeights.indices) {
                rowHeights[params.row] = max(rowHeights[params.row], child.measuredHeight)
            }
        }

        // Finally make enough vertical room for row-spanning cells.
        forEachCell { child, params ->
            if (params.rowSpan > 1) {
                distributeDeficit(
                    tracks = rowHeights,
                    start = params.row,
                    span = params.rowSpan,
                    requiredSize = child.measuredHeight,
                )
            }
        }

        // Give every cell its final spanned rectangle.
        forEachCell { child, params ->
            child.measure(
                MeasureSpec.makeMeasureSpec(
                    columnWidths.sumRange(params.column, params.columnSpan),
                    MeasureSpec.EXACTLY,
                ),
                MeasureSpec.makeMeasureSpec(
                    rowHeights.sumRange(params.row, params.rowSpan),
                    MeasureSpec.EXACTLY,
                ),
            )
        }

        val desiredWidth = paddingLeft + columnWidths.sum() + paddingRight
        val desiredHeight = paddingTop + rowHeights.sum() + paddingBottom
        setMeasuredDimension(
            resolveSize(max(desiredWidth, suggestedMinimumWidth), widthMeasureSpec),
            resolveSize(max(desiredHeight, suggestedMinimumHeight), heightMeasureSpec),
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val columnOffsets = columnWidths.offsets(paddingLeft)
        val rowOffsets = rowHeights.offsets(paddingTop)
        forEachCell { child, params ->
            val childLeft = columnOffsets.getOrElse(params.column) { paddingLeft }
            val childTop = rowOffsets.getOrElse(params.row) { paddingTop }
            val childRight = childLeft + columnWidths.sumRange(params.column, params.columnSpan)
            val childBottom = childTop + rowHeights.sumRange(params.row, params.rowSpan)
            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        dividerPaint.color = dividerColor
        dividerPaint.strokeWidth = dividerWidth
        forEachCell { child, params ->
            if (params.column + params.columnSpan < configuredColumnCount) {
                canvas.drawLine(
                    child.right.toFloat(),
                    child.top.toFloat(),
                    child.right.toFloat(),
                    child.bottom.toFloat(),
                    dividerPaint,
                )
            }
            if (params.row + params.rowSpan < configuredRowCount) {
                canvas.drawLine(
                    child.left.toFloat(),
                    child.bottom.toFloat(),
                    child.right.toFloat(),
                    child.bottom.toFloat(),
                    dividerPaint,
                )
            }
        }
    }

    private inline fun forEachCell(block: (View, TableCellLayoutParams) -> Unit) {
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child.visibility != GONE) {
                block(child, child.layoutParams as TableCellLayoutParams)
            }
        }
    }

    private fun distributeDeficit(
        tracks: IntArray,
        start: Int,
        span: Int,
        requiredSize: Int,
    ) {
        val actualSpan = span.coerceAtMost(tracks.size - start).coerceAtLeast(0)
        if (actualSpan == 0) return
        var deficit = requiredSize - tracks.sumRange(start, actualSpan)
        var tracksLeft = actualSpan
        for (index in start until start + actualSpan) {
            if (deficit <= 0) break
            val addition = (deficit + tracksLeft - 1) / tracksLeft
            tracks[index] += addition
            deficit -= addition
            tracksLeft--
        }
    }
}

private class TableCellLayoutParams(
    val row: Int,
    val column: Int,
    val rowSpan: Int,
    val columnSpan: Int,
) : ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

private fun NgaTableCell.tableLayoutParams() = TableCellLayoutParams(
    row = row,
    column = column,
    rowSpan = rowSpan,
    columnSpan = columnSpan,
)

private fun NgaTableCellAlignment.toGravity(): Int = when (this) {
    NgaTableCellAlignment.START -> Gravity.START
    NgaTableCellAlignment.CENTER -> Gravity.CENTER_HORIZONTAL
    NgaTableCellAlignment.END -> Gravity.END
}

private fun IntArray.sumRange(start: Int, span: Int): Int {
    if (start !in indices || span <= 0) return 0
    var sum = 0
    for (index in start until (start + span).coerceAtMost(size)) {
        sum += this[index]
    }
    return sum
}

private fun IntArray.offsets(initialOffset: Int): IntArray {
    val result = IntArray(size)
    var offset = initialOffset
    for (index in indices) {
        result[index] = offset
        offset += this[index]
    }
    return result
}

@SuppressLint("InlinedApi")
private fun LinearLayout.createHtmlTextView(
    html: String,
    appearance: TableAppearance,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
    onViewPostByPid: ((Int) -> Unit)?,
    fitImagesToWidth: Boolean,
): TextView = TextView(context).apply {
    includeFontPadding = false
    breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
    hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
    setTextSize(TypedValue.COMPLEX_UNIT_SP, appearance.bodyTextSizeSp)
    setTextColor(appearance.text)
    setLinkTextColor(appearance.link)
    movementMethod = CustomLinkMovementMethod(onViewPost, openUrl, onViewPostByPid)
    val normalizedHtml = html.replace(
        Regex("""\[img](https?://.+?)\[/img]""", RegexOption.IGNORE_CASE),
        """<img src="$1"/>""",
    )
    val parsed = normalizedHtml.parseAsHtml(
        HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS,
        CoilImageGetter(this, fitToTextViewWidth = fitImagesToWidth),
    )
    val builder = SpannableStringBuilder(parsed)
    BBCodeHandler.parse(context, builder)
    builder.trimTrailingLineBreaks()
    text = builder
}

private fun View.dp(value: Int): Int =
    (value * resources.displayMetrics.density).toInt()
