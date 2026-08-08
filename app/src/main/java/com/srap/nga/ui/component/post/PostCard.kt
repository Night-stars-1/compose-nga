package com.srap.nga.ui.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.srap.nga.utils.toHttps

/**
 * 帖子内容卡片
 */
@Composable
fun PostContentCard(
    avatar: String,
    name: String,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    message: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PostAuthor(
            avatar = avatar,
            name = name,
            onAvatarClick = onAvatarClick,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        ) {
            message()
        }
    }
}

/**
 * 评论卡片
 */
@Composable
fun PostReplyCard(
    avatar: String,
    name: String,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    message: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PostAuthor(
            avatar = avatar,
            name = name,
            onAvatarClick = onAvatarClick,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 56.dp, top = 6.dp),
        ) {
            message()
        }
    }
}

@Composable
private fun PostAuthor(
    avatar: String,
    name: String,
    onAvatarClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = avatar.toHttps(),
                contentDescription = "$name 的头像",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
