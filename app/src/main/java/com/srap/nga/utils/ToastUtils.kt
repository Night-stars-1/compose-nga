package com.srap.nga.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.srap.nga.myApplication

object ToastUtils {
    fun show(msg: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(myApplication, msg, Toast.LENGTH_SHORT).show()
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(myApplication, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}