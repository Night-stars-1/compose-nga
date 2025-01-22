package com.srap.nga.utils.provider

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class NgaProvider (
    private val context: Context,
    private val images: List<String>,
    private val openImage: (String, List<String>) -> Unit
) {

    @JavascriptInterface
    fun openImage(image: String) {
        Handler(Looper.getMainLooper()).post {
            openImage(image, images)
        }
    }
}