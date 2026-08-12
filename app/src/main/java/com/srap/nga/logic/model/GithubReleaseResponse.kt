package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName

/**
 * GitHub Release 信息，用于检查应用更新
 */
data class GithubReleaseResponse(
    @SerializedName("tag_name") val tagName: String = "",
    @SerializedName("name") val name: String? = null,
    @SerializedName("body") val body: String? = null,
    @SerializedName("html_url") val htmlUrl: String = "",
    @SerializedName("prerelease") val prerelease: Boolean = false,
    @SerializedName("assets") val assets: List<Asset> = listOf(),
) {
    data class Asset(
        @SerializedName("name") val name: String = "",
        @SerializedName("browser_download_url") val browserDownloadUrl: String = "",
    )

    /** APK 资源直链，Release 未附带 APK 时为 null */
    val apkDownloadUrl: String?
        get() = assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
            ?.browserDownloadUrl

    /** 浏览器打开地址，优先 APK 直链，无 APK 资源时退回 Release 页面 */
    val downloadUrl: String
        get() = apkDownloadUrl ?: htmlUrl
}
