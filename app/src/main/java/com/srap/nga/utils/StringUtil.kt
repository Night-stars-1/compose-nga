package com.srap.nga.utils

import java.security.MessageDigest
import kotlin.collections.joinToString
import kotlin.random.Random
import kotlin.text.format
import kotlin.text.toByteArray

object StringUtil {
    val UPPERCASELETTERS = 'A'..'Z'
    val LOWERCASELETTERS = 'a'..'z'
    val DIGiTS = '0'..'9'

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        return hash.joinToString("") { String.format("%02x", it) }
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun genString(length: Int, charPool: List<Char> = UPPERCASELETTERS+LOWERCASELETTERS+DIGiTS): String {
        return (1..length)
            .map { charPool.random() }
            .joinToString("")
    }
}