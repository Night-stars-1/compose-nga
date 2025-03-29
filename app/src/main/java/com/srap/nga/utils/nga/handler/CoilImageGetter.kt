package com.srap.nga.utils.nga.handler

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html.ImageGetter
import android.util.Log
import android.widget.TextView
import coil3.Image
import coil3.ImageLoader
import coil3.request.ImageRequest

open class CoilImageGetter(
    private val textView: TextView,
    private val imageLoader: ImageLoader = ImageLoader(textView.context),
    private val sourceModifier: ((source: String) -> String)? = null
) : ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val finalSource = sourceModifier?.invoke(source) ?: source
        val drawablePlaceholder = DrawablePlaceHolder(textView)
        imageLoader.enqueue(ImageRequest.Builder(textView.context).data(finalSource).apply {
            target(
                onError = {
                    Log.i("TAG", "getDrawable.onError: $source")
                },
                onSuccess = { image ->
                    Log.i("TAG", "getDrawable.onSuccess: $source")
                    drawablePlaceholder.updateImage(image)
                }
            )
        }.build())
        return drawablePlaceholder
    }

    private class DrawablePlaceHolder(private var textView: TextView) : BitmapDrawable() {

        private var image: Image? = null
        private var drawnOnce = false

        override fun draw(canvas: Canvas) {
            image?.draw(canvas)
            if (!drawnOnce) {
                drawnOnce = true
                // 再次刷新尝试解决文本布局异常
                textView.text = textView.text
            }
        }

        fun updateImage(image: Image) {
            this.image = image
            setBounds(0, 0, image.width, image.height)
            textView.text = textView.text
        }
    }
}