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
import androidx.compose.ui.Modifier
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
) {
    val newImages = images?.filter { !it.first.contains(".mp4") }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (!newImages.isNullOrEmpty() && maxImageCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                ImagesPreviewer(
                    images = newImages,
                    maxVisibleImages = maxImageCount,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.72f),
                )
                IconText(
                    text = count.toString(),
                    icon = Icons.AutoMirrored.Filled.Chat,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.72f),
                )
            }
        }
    }
}
