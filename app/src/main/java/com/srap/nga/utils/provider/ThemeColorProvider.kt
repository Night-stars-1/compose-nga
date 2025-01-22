package com.srap.nga.utils.provider

import android.content.Context
import android.webkit.JavascriptInterface

class ThemeColorProvider(
    private val context: Context,
    private val themeColors: Map<String, String>
) {

    @JavascriptInterface
    fun getThemeColors(): String {
        // 将主题色转换为 JSON 格式
        return themeColors.entries.joinToString(
            prefix = "{",
            postfix = "}",
            separator = ","
        ) { "\"${it.key}\": \"${it.value}\"" }
    }
}
