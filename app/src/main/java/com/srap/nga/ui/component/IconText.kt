package com.srap.nga.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 图标文本组件
 * 左图右字
 */
@Composable
fun IconText(
    text: String,
    icon: ImageVector,
) {
    val color = MaterialTheme.colorScheme.outline
    Row(horizontalArrangement = Arrangement.Center) {
        Image(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier
                .padding(horizontal = 1.dp)
                .padding(top = 2.dp)
                .size(16.dp),
            colorFilter = ColorFilter.tint(color),
        )
        Text(
            text = text,
            lineHeight = 14.sp,
            fontSize = 14.sp,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}