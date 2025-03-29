package com.srap.nga.utils.nga.handler

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.appcompat.content.res.AppCompatResources
import com.srap.nga.utils.EmojiUtils
import java.util.regex.Pattern


object BBCodeHandler {
    fun parse(mContext: Context, builder: SpannableStringBuilder) {
        val pattern = Pattern.compile("\\[[^]]+]")
        val matcher = pattern.matcher(builder)
        while (matcher.find()) {
            val group = matcher.group()
            if (group.startsWith("[s:")) {
                emoji(mContext, builder, group, matcher.start(), matcher.end())
            }
        }
    }

    private fun emoji(
        mContext: Context,
        builder: SpannableStringBuilder,
        tag: String,
        start: Int,
        end: Int,
    ): SpannableStringBuilder {
        EmojiUtils.emojiMap[tag]?.let {
            AppCompatResources.getDrawable(mContext, it)?.let { emoji ->
                emoji.setBounds(0, 0, emoji.intrinsicWidth / 3, emoji.intrinsicHeight / 3)
                builder.setSpan(ImageSpan(emoji), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return builder
    }
}
