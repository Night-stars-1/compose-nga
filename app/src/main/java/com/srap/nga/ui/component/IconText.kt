package com.srap.nga.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * 图标文本组件
 * 左图右字
 */
@Composable
fun IconText(
    text: String,
    icon: ImageVector,
    color: Color? = null,
) {
    val contentColor = color ?: MaterialTheme.colorScheme.onSurfaceVariant
    Row(horizontalArrangement = Arrangement.Center) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 1.dp)
                .padding(top = 2.dp)
                .size(16.dp),
            tint = contentColor,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
