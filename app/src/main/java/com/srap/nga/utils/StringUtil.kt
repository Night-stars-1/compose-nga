package com.srap.nga.utils

import com.srap.nga.constant.Constants.EMPTY_STRING
import java.security.MessageDigest
import kotlin.collections.joinToString
import kotlin.text.format
import kotlin.text.toByteArray

object StringUtil {
    val UPPERCASE_LETTERS = 'A'..'Z'
    val LOWERCASE_LETTERS = 'a'..'z'
    val DIGiTS = '0'..'9'

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        return hash.joinToString(EMPTY_STRING) { String.format("%02x", it) }
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString(EMPTY_STRING) { "%02x".format(it) }
    }

    fun genString(length: Int, charPool: List<Char> = UPPERCASE_LETTERS+LOWERCASE_LETTERS+DIGiTS): String {
        return (1..length)
            .map { charPool.random() }
            .joinToString(EMPTY_STRING)
    }
}