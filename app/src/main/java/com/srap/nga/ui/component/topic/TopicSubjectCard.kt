package com.srap.nga.ui.component.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.srap.nga.ui.component.IconText
import com.srap.nga.ui.component.ImagesPreviewer

@Composable
fun TopicSubjectCard(
    title: String,
    images: List<String>?,
    name: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.outline
    Card(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title)
            if (images != null) {
                ImagesPreviewer(images)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    name,
                    fontSize = 14.sp,
                    color = color
                )
                IconText(count.toString(), Icons.AutoMirrored.Filled.Chat)
            }
        }
    }
}