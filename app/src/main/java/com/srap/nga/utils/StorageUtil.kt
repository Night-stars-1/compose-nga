package com.srap.nga.utils

import android.content.Context
import android.content.SharedPreferences
import com.srap.nga.myApplication
import androidx.core.content.edit

object StorageUtil {
    private val pref: SharedPreferences = myApplication.baseContext.getSharedPreferences("ngaPrefs", Context.MODE_PRIVATE)
    private const val TOKEN = "Token"
    private const val UID = "Uid"

    private var _token = pref.getString(TOKEN, "").toString()

    // 用户Token
    var Token: String
        get() = _token
        set(value) {
            _token = value
            pref.edit { putString(TOKEN, value) }
        }

    private var _uid = pref.getInt(UID, 0)

    // 用户UID
    var Uid: Int
        get() = _uid
        set(value) {
            _uid = value
            pref.edit { putInt(UID, value) }
        }
}