package com.srap.nga.ui.component.webview

private val ngaHostPattern = Regex(
    """^https?://(?:[^/?#]+\.)*(?:nga\.cn|ngabbs\.com)(?::\d+)?(?:[/?#]|$)""",
    RegexOption.IGNORE_CASE,
)
private val tidQueryPattern = Regex("""(?:[?&])tid=(\d+)""", RegexOption.IGNORE_CASE)
private val pidQueryPattern = Regex("""(?:[?&])pid=(\d+)""", RegexOption.IGNORE_CASE)
private val ucpFuncPattern = Regex("""(?:[?&])func=ucp(?:[&#]|$)""", RegexOption.IGNORE_CASE)
private val uidQueryPattern = Regex("""(?:[?&])uid=(\d+)""", RegexOption.IGNORE_CASE)

internal data class NgaPostLink(
    val tid: Int?,
    val pid: Int?,
)

internal fun parseNgaPostLink(url: String): NgaPostLink? {
    if (!ngaHostPattern.containsMatchIn(url)) return null

    val normalizedUrl = url.replace("&amp;", "&", ignoreCase = true)
    val tid = tidQueryPattern.find(normalizedUrl)
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
    val pid = pidQueryPattern.find(normalizedUrl)
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }

    return if (tid == null && pid == null) null else NgaPostLink(tid, pid)
}

/** 解析 NGA 用户中心链接（nuke.php?func=ucp&uid=xxx），返回用户 uid。 */
fun parseNgaUserLink(url: String): Int? {
    if (!ngaHostPattern.containsMatchIn(url)) return null

    val normalizedUrl = url.replace("&amp;", "&", ignoreCase = true)
    if (!normalizedUrl.contains("nuke.php", ignoreCase = true)) return null
    if (!ucpFuncPattern.containsMatchIn(normalizedUrl)) return null

    return uidQueryPattern.find(normalizedUrl)
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
        ?.takeIf { it > 0 }
}
