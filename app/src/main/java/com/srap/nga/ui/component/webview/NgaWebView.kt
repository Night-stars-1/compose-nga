package com.srap.nga.ui.component.webview

import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import com.srap.nga.myApplication
import com.srap.nga.utils.nga.handler.BBCodeHandler
import com.srap.nga.utils.nga.handler.CoilImageGetter
import com.srap.nga.utils.nga.parse.NgaMarkupNormalizer

internal const val NGA_HTML_PARSE_FLAGS =
    HtmlCompat.FROM_HTML_MODE_COMPACT or HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS

fun openInBrowser(url: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, url)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    myApplication.startActivity(intent)
}

fun openInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    myApplication.startActivity(intent)
}

class CustomLinkMovementMethod(
    private val onViewPost: (Int) -> Unit,
    private val openUrl: (String) -> Unit,
    private val onViewPostByPid: ((Int) -> Unit)? = null,
) : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return super.onTouchEvent(widget, buffer, event)

        val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
        val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY
        val layout = widget.layout
        val line = layout.getLineForVertical(y)
        val offset = layout.getOffsetForHorizontal(line, x.toFloat())

        buffer.getSpans(offset, offset, URLSpan::class.java).firstOrNull()?.let { span ->
            handleCustomNavigation(span.url)
            return true
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    private fun handleCustomNavigation(url: String) {
        val uri = url.toUri()
        parseNgaPostLink(url)?.let { link ->
            if (link.pid != null && onViewPostByPid != null) {
                onViewPostByPid.invoke(link.pid)
                return
            }
            link.tid?.let {
                onViewPost(it)
                return
            }
            if (link.pid != null) {
                openUrl(url)
                return
            }
        }
        if (url.contains("nga", ignoreCase = true) && url.contains("tid", ignoreCase = true)) {
            uri.getQueryParameter("tid")?.toIntOrNull()?.let(onViewPost)
            return
        }
        if (parseNgaUserLink(url) != null) {
            // 交给 openUrl 统一分发，会跳转到应用内用户页面。
            openUrl(url)
            return
        }
        if (url.contains("nga.cn/", ignoreCase = true)) {
            openUrl(url)
            return
        }
        openInBrowser(uri)
    }
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
    onViewPostByPid: ((Int) -> Unit)? = null,
) {
    val contentColor = LocalContentColor.current.toArgb()
    val primary = MaterialTheme.colorScheme.primary.toArgb()
    val bodyTextSizeSp = MaterialTheme.typography.bodyMedium.fontSize.value
    val adaptedHtml = NgaMarkupNormalizer.adaptStyles(html)

    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                updateNgaHtmlText(
                    html = adaptedHtml,
                    contentColor = contentColor,
                    linkColor = primary,
                    textSizeSp = bodyTextSizeSp,
                    onViewPost = onViewPost,
                    openUrl = openUrl,
                    onViewPostByPid = onViewPostByPid,
                )
            }
        },
        update = { textView ->
            textView.updateNgaHtmlText(
                html = adaptedHtml,
                contentColor = contentColor,
                linkColor = primary,
                textSizeSp = bodyTextSizeSp,
                onViewPost = onViewPost,
                openUrl = openUrl,
                onViewPostByPid = onViewPostByPid,
            )
        },
    )
}

private fun TextView.updateNgaHtmlText(
    html: String,
    contentColor: Int,
    linkColor: Int,
    textSizeSp: Float,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
    onViewPostByPid: ((Int) -> Unit)?,
) {
    includeFontPadding = false
    setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp)
    movementMethod = CustomLinkMovementMethod(onViewPost, openUrl, onViewPostByPid)
    setTextColor(contentColor)
    setLinkTextColor(linkColor)
    if (tag == html) return

    val parsed = html.parseAsHtml(
        NGA_HTML_PARSE_FLAGS,
        CoilImageGetter(this),
    )
    val builder = SpannableStringBuilder(parsed)
    BBCodeHandler.parse(context, builder)
    builder.trimTrailingLineBreaks()
    text = builder
    tag = html
}

internal fun SpannableStringBuilder.trimTrailingLineBreaks() {
    while (isNotEmpty() && (last() == '\n' || last() == '\r')) {
        delete(length - 1, length)
    }
}
