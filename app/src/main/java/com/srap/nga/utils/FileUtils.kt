package com.srap.nga.utils

import com.srap.nga.myApplication

object FileUtils {
    fun readAsset(fileName: String): String {
        return myApplication.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}