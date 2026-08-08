package com.srap.nga.ui.post

import android.app.Activity
import android.content.Context
import android.content.Intent

private const val NGA_POST_URL = "https://bbs.nga.cn/read.php?tid="

internal fun buildPostShareText(id: Int, title: String?): String = buildString {
    title?.trim()?.takeIf { it.isNotEmpty() }?.let {
        appendLine(it)
    }
    append(NGA_POST_URL)
    append(id)
}

internal fun Context.sharePost(id: Int, title: String?) {
    val normalizedTitle = title?.trim()?.takeIf { it.isNotEmpty() }
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        normalizedTitle?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        putExtra(Intent.EXTRA_TEXT, buildPostShareText(id, normalizedTitle))
    }
    val chooser = Intent.createChooser(sendIntent, "分享帖子")
    if (this !is Activity) {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(chooser)
}
