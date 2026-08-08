package com.srap.nga

import android.app.Application
import android.content.Context
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

lateinit var myApplication: MyApplication

@HiltAndroidApp
class MyApplication : Application(), SingletonImageLoader.Factory {
    // TODO Delete when https://github.com/google/dagger/issues/3601 is resolved.
    @Inject @ApplicationContext lateinit var context: Context

    override fun onCreate() {
        super.onCreate()

        myApplication = this
    }

    override fun newImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
}