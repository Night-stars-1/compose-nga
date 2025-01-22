package com.srap.nga.logic.state

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

sealed class LoadingState<out T> : State() {
    inline fun <reified T> parseItem(item: Any?): List<T>? {
        return try {
            val gson = Gson()
            val json = gson.toJson(item)
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e("parseItem", "Failed to parse item: $e")
            null
        }
    }

    data class Success<out T>(val response: T) : LoadingState<T>() {
        inline fun <reified T> data(): List<T>? {
            val result = response as List<*>
            val data = result[0]
            return parseItem(data)
        }
    }
    data class Error(val errMsg: String) : LoadingState<Nothing>()
}
