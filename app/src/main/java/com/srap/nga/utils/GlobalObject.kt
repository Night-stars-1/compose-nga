package com.srap.nga.utils

import androidx.navigation.NavHostController
import java.lang.ref.WeakReference

object GlobalObject {
    private var navControllerRef: WeakReference<NavHostController>? = null
    // 用户是否登录
    var isLogin = true

    var navController: NavHostController?
        get() = navControllerRef?.get()
        set(value) {
            navControllerRef = if (value != null) WeakReference(value) else null
        }
}