package com.srap.nga.ui.component.webview

import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.srap.nga.myApplication
import kotlin.text.toInt
import androidx.core.text.parseAsHtml
import androidx.core.net.toUri
import com.srap.nga.utils.nga.handler.BBCodeHandler
import com.srap.nga.utils.nga.handler.CoilImageGetter

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
) : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView , buffer: Spannable , event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return super.onTouchEvent(widget, buffer, event)

        val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
        val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY

        val layout = widget.layout
        val line = layout.getLineForVertical(y)
        val off = layout.getOffsetForHorizontal(line, x.toFloat())

        buffer.getSpans(off, off, URLSpan::class.java).firstOrNull()?.let { span ->
            // 拦截点击事件
            handleCustomNavigation(span.url)
            return true
        }

        return super.onTouchEvent(widget, buffer, event)
    }

    private fun handleCustomNavigation(url: String) {
        val uri = url.toUri()
        if (url.contains("nga") && url.contains("tid")) {
            val tid = uri.getQueryParameter("tid")
            if (tid != null) {
                onViewPost(tid.toInt())
            }
            return
        }
        if (url.contains("nga.cn/")) {
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
) {
    val contentColor = LocalContentColor.current.toArgb()
    val primary = MaterialTheme.colorScheme.primary.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                movementMethod = CustomLinkMovementMethod(onViewPost, openUrl)
                setTextColor(contentColor)
                setLinkTextColor(primary)
                val msg = html.parseAsHtml(
                    HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS,
                    CoilImageGetter(this)
                )
                val builder = SpannableStringBuilder(msg)
                BBCodeHandler.parse(context, builder)
                text = builder
            }
        }
    )
}