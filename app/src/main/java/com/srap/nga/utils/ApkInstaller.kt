package com.srap.nga.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * 应用内更新：下载 APK 并调起系统安装器
 */
object ApkInstaller {

    private val client = OkHttpClient()

    /**
     * 下载 APK 到应用缓存目录
     * @param onProgress 下载进度回调，0..100，仅在百分比变化时回调
     */
    fun download(context: Context, url: String, onProgress: (Int) -> Unit): File {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            val body = response.body
            val dir = File(context.cacheDir, "updates").apply { mkdirs() }
            val file = File(dir, "update.apk")
            val total = body.contentLength()
            var downloaded = 0L
            var lastPercent = -1
            body.byteStream().use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        downloaded += read
                        if (total > 0) {
                            val percent = (downloaded * 100 / total).toInt()
                            if (percent != lastPercent) {
                                lastPercent = percent
                                onProgress(percent)
                            }
                        }
                    }
                }
            }
            return file
        }
    }

    /**
     * 调起系统安装器安装 APK
     */
    fun install(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
