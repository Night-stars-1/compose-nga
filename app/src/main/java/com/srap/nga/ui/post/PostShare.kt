package com.srap.nga.ui.post

import android.app.Activity
import android.content.Context
import android.content.Intent

private const val NGA_POST_URL = "https://bbs.nga.cn/read.php?tid="

internal fun buildPostShareText(id: Int): String = "$NGA_POST_URL$id"

internal fun Context.sharePost(id: Int) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, buildPostShareText(id))
    }
    val chooser = Intent.createChooser(sendIntent, "分享帖子")
    if (this !is Activity) {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(chooser)
}