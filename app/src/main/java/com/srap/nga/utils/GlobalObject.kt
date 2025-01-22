package com.srap.nga.utils

import androidx.navigation.NavHostController
import java.lang.ref.WeakReference

object GlobalObject {
    private var navControllerRef: WeakReference<NavHostController>? = null

    var navController: NavHostController?
        get() = navControllerRef?.get()
        set(value) {
            navControllerRef = if (value != null) WeakReference(value) else null
        }
}