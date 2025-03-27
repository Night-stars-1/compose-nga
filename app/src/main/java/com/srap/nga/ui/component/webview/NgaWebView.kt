package com.srap.nga.ui.component.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html.ImageGetter
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil3.Image
import coil3.ImageLoader
import com.srap.nga.BuildConfig
import com.srap.nga.myApplication
import com.srap.nga.utils.ThemeUtil
import com.srap.nga.utils.provider.NgaProvider
import com.srap.nga.utils.provider.ThemeColorProvider
import kotlin.text.toInt
import androidx.core.text.parseAsHtml
import androidx.core.net.toUri
import com.srap.nga.constant.Constants.UTF8

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NgaWebView(
    html: String,
    images: List<String>,
    onViewPost: (Int) -> Unit,
    openImage: (String, List<String>) -> Unit
) {
    val context = LocalContext.current
    val themeColors = ThemeUtil.getThemeColors()
    val webView = remember {
        WebView(context).apply {
            setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            settings.javaScriptEnabled = false
            settings.domStorageEnabled = false
            settings.loadWithOverviewMode = false
            settings.useWideViewPort = true
            // 禁止网络图片加载
            settings.blockNetworkImage = true
            // 滚动条
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
//                webViewClient = WebViewClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    // 允许加载网络图片
                    settings.blockNetworkImage = false
                }
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request == null) {
                        return true
                    }
                    val url = request.url.toString()
                    if (url.contains("nga") && url.contains("tid")) {
                        val tid = request.url.getQueryParameter("tid")
                        if (tid != null) {
                            onViewPost(tid.toInt())
                        }
                    } else {
                        openInBrowser(request.url)
                    }
                    return true
                }
            }
            addJavascriptInterface(
                ThemeColorProvider(context, themeColors),
                "ThemeProvider"
            )
            addJavascriptInterface(
                NgaProvider(context, images, openImage),
                "NgaProvider"
            )
            loadDataWithBaseURL(null, html, "text/html", UTF8, null)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { webView }
    )
}

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

open class CoilImageGetter(
    private val textView: TextView,
    private val imageLoader: ImageLoader = ImageLoader(textView.context),
    private val sourceModifier: ((source: String) -> String)? = null
) : ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val finalSource = sourceModifier?.invoke(source) ?: source

        val drawablePlaceholder = DrawablePlaceHolder()
//        imageLoader.enqueue(ImageRequest.Builder(textView.context).data(finalSource).apply {
//            target(
//                onError = {
//                    Log.i("TAG", "getDrawable.onError: $source")
//                },
//                onSuccess = { image ->
//                    drawablePlaceholder.updateImage(image)
//                    textView.text = textView.text
//                }
//            )
//        }.build())
        return drawablePlaceholder
    }

    private class DrawablePlaceHolder : BitmapDrawable() {

        private var image: Image? = null

        override fun draw(canvas: Canvas) {
            image?.draw(canvas)
            setBounds(0, 0, 100, 100)
        }

        fun updateImage(image: Image) {
            this.image = image
            setBounds(0, 0, image.width, image.height)
        }
    }
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
                text = html.parseAsHtml(
                    HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS,
                    CoilImageGetter(this)
                )
            }
        }
    )
}