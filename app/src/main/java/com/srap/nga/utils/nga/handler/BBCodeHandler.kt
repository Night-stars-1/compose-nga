package com.srap.nga.utils.nga.handler

import android.content.Context
import android.content.res.Configuration
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import androidx.appcompat.content.res.AppCompatResources
import com.srap.nga.utils.EmojiUtils
import java.util.regex.Pattern

object BBCodeHandler {
    private val darkThemeColors = mapOf(
        0xFFD32F2F.toInt() to 0xFFF2B8B5.toInt(),
        0xFFC2185B.toInt() to 0xFFFFB0C8.toInt(),
        0xFFE64A19.toInt() to 0xFFFFB59E.toInt(),
        0xFFF57C00.toInt() to 0xFFFFB77C.toInt(),
        0xFFF9A825.toInt() to 0xFFE8C442.toInt(),
        0xFF388E3C.toInt() to 0xFFA5D6A7.toInt(),
        0xFF2E7D32.toInt() to 0xFF81C784.toInt(),
        0xFF1976D2.toInt() to 0xFFA8C7FA.toInt(),
        0xFF1565C0.toInt() to 0xFFB6C4FF.toInt(),
        0xFF7B1FA2.toInt() to 0xFFD0BCFF.toInt(),
        0xFF757575.toInt() to 0xFFCAC4D0.toInt(),
    )

    fun parse(context: Context, builder: SpannableStringBuilder) {
        adaptForegroundColors(context, builder)

        val pattern = Pattern.compile("\\[[^]]+]")
        val matcher = pattern.matcher(builder)
        while (matcher.find()) {
            val group = matcher.group()
            if (group.startsWith("[s:")) {
                emoji(context, builder, group, matcher.start(), matcher.end())
            }
        }
    }

    internal fun darkThemeColor(color: Int): Int = darkThemeColors[color] ?: color

    private fun adaptForegroundColors(context: Context, builder: SpannableStringBuilder) {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightMode != Configuration.UI_MODE_NIGHT_YES) return

        builder.getSpans(0, builder.length, ForegroundColorSpan::class.java).forEach { span ->
            val adaptedColor = darkThemeColor(span.foregroundColor)
            if (adaptedColor == span.foregroundColor) return@forEach

            val start = builder.getSpanStart(span)
            val end = builder.getSpanEnd(span)
            val flags = builder.getSpanFlags(span)
            builder.removeSpan(span)
            builder.setSpan(ForegroundColorSpan(adaptedColor), start, end, flags)
        }
    }

    private fun emoji(
        context: Context,
        builder: SpannableStringBuilder,
        tag: String,
        start: Int,
        end: Int,
    ): SpannableStringBuilder {
        EmojiUtils.emojiMap[tag]?.let {
            AppCompatResources.getDrawable(context, it)?.let { emoji ->
                emoji.setBounds(0, 0, emoji.intrinsicWidth / 3, emoji.intrinsicHeight / 3)
                builder.setSpan(
                    ImageSpan(emoji),
                    start,
                    end,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
        }

        return builder
    }
}
