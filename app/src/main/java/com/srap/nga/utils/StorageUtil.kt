package com.srap.nga.utils

import android.content.Context
import android.content.SharedPreferences
import com.srap.nga.myApplication

object StorageUtil {
    private val pref: SharedPreferences = myApplication.baseContext.getSharedPreferences("ngaPrefs", Context.MODE_PRIVATE)
    private const val TOKEN = "Token"
    private const val UID = "Uid"

    private var _token = pref.getString(TOKEN, "").toString()

    var Token: String
        get() = _token
        set(value) {
            _token = value
            pref.edit().putString(TOKEN, value).apply()
        }

    private var _uid = pref.getInt(UID, 0)

    var Uid: Int
        get() = _uid
        set(value) {
            _uid = value
            pref.edit().putInt(UID, value).apply()
        }
}