package com.srap.nga.ui.component.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.srap.nga.constant.Constants.NGA_TAG
import com.srap.nga.constant.Constants.UTF8
import com.srap.nga.utils.StorageUtils

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    url: String,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    onUpdateProgress: (Float) -> Unit,
    onUpdateTitle: (String) -> Unit,
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                CookieManager.getInstance().let {
                    it.setAcceptCookie(true)
                    it.setAcceptThirdPartyCookies(this@apply, true)
                    it.setCookie(".nga.cn", "access_uid=${StorageUtils.Uid}")
                    it.setCookie(".nga.cn", "access_token=${StorageUtils.Token}")
                }

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    blockNetworkImage = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    defaultTextEncodingName = UTF8
                    allowContentAccess = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    javaScriptCanOpenWindowsAutomatically = true
                    loadsImagesAutomatically = true
                    allowFileAccess = false
                    userAgentString = "$userAgentString $NGA_TAG"
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {

                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView?) {
                        super.onCloseWindow(window)
                        onFinish()
                    }

                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        onUpdateProgress(newProgress / 100f)
                    }

                    override fun onReceivedTitle(view: WebView, title: String) {
                        super.onReceivedTitle(view, title)
                        onUpdateTitle(title)
                    }
                }

                loadUrl(url)
            }
        },
        update = {

        },
    )
}