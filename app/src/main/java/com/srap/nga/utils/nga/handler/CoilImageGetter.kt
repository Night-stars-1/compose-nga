package com.srap.nga.utils.nga.handler

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.text.Html.ImageGetter
import android.view.View
import android.widget.TextView
import coil3.Image
import coil3.ImageLoader
import coil3.asDrawable
import coil3.imageLoader
import coil3.request.ImageRequest
import kotlin.math.min
import kotlin.math.roundToInt

open class CoilImageGetter(
    private val textView: TextView,
    private val imageLoader: ImageLoader = textView.context.imageLoader,
    private val sourceModifier: ((source: String) -> String)? = null,
    private val fitToTextViewWidth: Boolean = false,
) : ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val finalSource = sourceModifier?.invoke(source) ?: source
        val encodedSize = NgaImageSizeParser.parse(finalSource)
        val drawablePlaceholder = DrawablePlaceHolder(
            textView = textView,
            fitToTextViewWidth = fitToTextViewWidth,
            initialSize = encodedSize,
        )
        val request = ImageRequest.Builder(textView.context)
            .data(finalSource)
            .apply {
                if (fitToTextViewWidth) {
                    val maximumWidth = textView.availableImageWidth()
                    val decodeSize = encodedSize?.fitWithin(maximumWidth)
                    if (decodeSize == null) {
                        size(maximumWidth)
                    } else {
                        size(decodeSize.width, decodeSize.height)
                    }
                }
                target(
                    onSuccess = { image -> drawablePlaceholder.updateImage(image) },
                )
            }
            .build()
        imageLoader.enqueue(request)
        return drawablePlaceholder
    }

    private class DrawablePlaceHolder(
        private val textView: TextView,
        private val fitToTextViewWidth: Boolean,
        initialSize: NgaImageSize?,
    ) : Drawable(), Drawable.Callback {
        private var naturalSize = initialSize
        private var imageDrawable: Drawable? = null

        private val layoutChangeListener = View.OnLayoutChangeListener {
                _, left, _, right, _, oldLeft, _, oldRight, _ ->
            if (right - left != oldRight - oldLeft && updateBounds()) {
                refreshTextLayout()
            }
        }

        init {
            updateBounds()
            if (fitToTextViewWidth) {
                textView.addOnLayoutChangeListener(layoutChangeListener)
            }
        }

        override fun draw(canvas: Canvas) {
            imageDrawable?.let { drawable ->
                drawable.bounds = bounds
                drawable.draw(canvas)
            }
        }

        override fun setAlpha(alpha: Int) {
            imageDrawable?.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            imageDrawable?.colorFilter = colorFilter
        }

        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

        fun updateImage(image: Image) {
            naturalSize = NgaImageSize(
                width = image.width.coerceAtLeast(1),
                height = image.height.coerceAtLeast(1),
            )
            (imageDrawable as? Animatable)?.stop()
            imageDrawable?.callback = null
            imageDrawable = image.asDrawable(textView.resources).also { drawable ->
                drawable.callback = this
            }
            if (updateBounds()) {
                refreshTextLayout()
            } else {
                textView.invalidate()
            }
            (imageDrawable as? Animatable)?.start()
        }

        override fun invalidateDrawable(who: Drawable) {
            textView.postInvalidateOnAnimation()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, whenMillis: Long) {
            val delayMillis = (whenMillis - SystemClock.uptimeMillis()).coerceAtLeast(0L)
            textView.postDelayed(what, delayMillis)
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            textView.removeCallbacks(what)
        }

        private fun updateBounds(): Boolean {
            val size = naturalSize ?: return false
            val targetSize = if (fitToTextViewWidth) {
                size.fitWithin(textView.availableImageWidth())
            } else {
                size
            }
            if (bounds.width() == targetSize.width && bounds.height() == targetSize.height) {
                imageDrawable?.bounds = bounds
                return false
            }

            setBounds(0, 0, targetSize.width, targetSize.height)
            imageDrawable?.setBounds(0, 0, targetSize.width, targetSize.height)
            return true
        }

        private fun refreshTextLayout() {
            textView.text = textView.text
        }
    }
}

private fun TextView.availableImageWidth(): Int =
    (width - totalPaddingLeft - totalPaddingRight)
        .takeIf { it > 0 }
        ?: (resources.displayMetrics.widthPixels - totalPaddingLeft - totalPaddingRight)
            .coerceAtLeast(1)

private fun NgaImageSize.fitWithin(maximumWidth: Int): NgaImageSize {
    val targetWidth = min(width, maximumWidth.coerceAtLeast(1))
    val targetHeight = (height * targetWidth.toFloat() / width)
        .roundToInt()
        .coerceAtLeast(1)
    return NgaImageSize(targetWidth, targetHeight)
}
