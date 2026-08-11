package com.srap.nga.ui.component.webview

private val ngaHostPattern = Regex(
    """^https?://(?:[^/?#]+\.)*(?:nga\.cn|ngabbs\.com)(?::\d+)?(?:[/?#]|$)""",
    RegexOption.IGNORE_CASE,
)
private val tidQueryPattern = Regex("""(?:[?&])tid=(\d+)""", RegexOption.IGNORE_CASE)
private val pidQueryPattern = Regex("""(?:[?&])pid=(\d+)""", RegexOption.IGNORE_CASE)

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
