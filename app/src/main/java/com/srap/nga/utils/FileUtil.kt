package com.srap.nga.utils

import com.srap.nga.myApplication

object FileUtil {
    fun readAsset(fileName: String): String {
        return myApplication.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}