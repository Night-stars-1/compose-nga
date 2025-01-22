package com.srap.nga

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

lateinit var myApplication: MyApplication

@HiltAndroidApp
class MyApplication : Application() {
    // TODO Delete when https://github.com/google/dagger/issues/3601 is resolved.
    @Inject @ApplicationContext lateinit var context: Context

    override fun onCreate() {
        super.onCreate()

        myApplication = this
    }
}