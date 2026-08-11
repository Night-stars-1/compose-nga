package com.srap.nga.ui.component.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.srap.nga.ui.component.IconText
import com.srap.nga.ui.component.ImagesPreviewer

@Composable
fun TopicSubjectCard(
    title: String,
    name: String,
    count: Int,
    modifier: Modifier = Modifier,
    images: List<Pair<String, String>>? = emptyList(),
    maxImageCount: Int = Int.MAX_VALUE,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val newImages = images?.filter { !it.first.contains(".mp4", ignoreCase = true) }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp,
            ),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (!newImages.isNullOrEmpty() && maxImageCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                ImagesPreviewer(
                    images = newImages,
                    maxVisibleImages = maxImageCount,
                    imageHeight = 104.dp,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconText(
                    text = count.toString(),
                    icon = Icons.AutoMirrored.Filled.Chat,
                    color = contentColor.copy(alpha = 0.72f),
                )
            }
        }
    }
}
