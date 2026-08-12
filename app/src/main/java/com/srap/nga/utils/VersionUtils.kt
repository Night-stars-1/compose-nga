package com.srap.nga.utils

object VersionUtils {

    /**
     * 判断远端版本号是否比当前版本更新，兼容 "v1.2.3" 与 "1.2.3" 形式
     */
    fun isNewerVersion(remote: String, current: String): Boolean {
        val remoteParts = parse(remote)
        val currentParts = parse(current)
        if (remoteParts.isEmpty() || currentParts.isEmpty()) return false
        val length = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until length) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r != c) return r > c
        }
        return false
    }

    private fun parse(version: String): List<Int> = version
        .trim()
        .removePrefix("v")
        .removePrefix("V")
        .substringBefore('-')
        .split('.')
        .mapNotNull { it.toIntOrNull() }
}
