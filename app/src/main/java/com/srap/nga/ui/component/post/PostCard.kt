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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.ui.component.UserAvatar
import com.srap.nga.utils.toHttps
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 帖子内容卡片
 */
@Composable
fun PostContentCard(
    avatar: String,
    name: String,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    isFollowing: Boolean = false,
    followLoading: Boolean = false,
    onFollowClick: (() -> Unit)? = null,
    group: String = "",
    rvrc: String = "",
    posts: Int = 0,
    medals: List<PostResponse.Result.Medal> = emptyList(),
    forumName: String = "",
    onForumClick: (() -> Unit)? = null,
    postDate: String = "",
    message: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
        ) {
        PostAuthor(
            avatar = avatar,
            name = name,
            onAvatarClick = onAvatarClick,
            isFollowing = isFollowing,
            followLoading = followLoading,
            onFollowClick = onFollowClick,
            group = group,
            rvrc = rvrc,
            posts = posts,
            medals = medals,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        ) {
            message()
        }
        PostMetadata(
            forumName = forumName,
            onForumClick = onForumClick,
            postDate = postDate,
        )
        }
    }
}/**
 * 评论卡片
 */
@Composable
fun PostReplyCard(
    avatar: String,
    name: String,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    group: String = "",
    rvrc: String = "",
    posts: Int = 0,
    medals: List<PostResponse.Result.Medal> = emptyList(),
    postDate: String = "",
    message: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        PostAuthor(
            avatar = avatar,
            name = name,
            onAvatarClick = onAvatarClick,
            group = group,
            rvrc = rvrc,
            posts = posts,
            medals = medals,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 56.dp, top = 6.dp),
        ) {
            message()
        }
        PostMetadata(postDate = postDate)
        }
    }
}@Composable
private fun PostAuthor(
    avatar: String,
    name: String,
    onAvatarClick: () -> Unit,
    isFollowing: Boolean = false,
    followLoading: Boolean = false,
    onFollowClick: (() -> Unit)? = null,
    group: String = "",
    rvrc: String = "",
    posts: Int = 0,
    medals: List<PostResponse.Result.Medal> = emptyList(),
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
            UserAvatar(
                avatar = avatar,
                name = name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
            val profile = buildList {
                if (group.isNotBlank()) add("级别:$group")
                if (rvrc.isNotBlank()) add("威望:$rvrc")
                if (posts > 0) add("发帖:$posts")
            }.joinToString(" ")
            if (profile.isNotBlank()) {
                Text(
                    text = profile,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            if (medals.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    medals.forEach { medal ->
                        medal.icon
                            ?.takeIf { it.isNotBlank() }
                            ?.let { icon ->
                                AsyncImage(
                                    model = icon.toHttps(),
                                    contentDescription = medal.name.orEmpty().ifBlank { "勋章" },
                                    modifier = Modifier
                                        .padding(top = 2.dp, end = 4.dp)
                                        .size(24.dp),
                                )
                            }
                    }
                }
            }
        }
        if (onFollowClick != null) {
            FilledTonalButton(
                onClick = onFollowClick,
                enabled = !followLoading,
                contentPadding = PaddingValues(horizontal = 10.dp),
            ) {
                Icon(
                    imageVector = if (isFollowing) {
                        Icons.Outlined.PersonRemove
                    } else {
                        Icons.Outlined.PersonAdd
                    },
                    contentDescription = if (isFollowing) "取消关注" else "关注",
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(if (isFollowing) "已关注" else "关注")
            }
        }
    }
}

private val postTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

@Composable
private fun PostMetadata(
    forumName: String = "",
    onForumClick: (() -> Unit)? = null,
    postDate: String = "",
) {
    val formattedTime = remember(postDate) {
        val normalized = postDate.trim()
        val timestamp = normalized.toLongOrNull()
        if (timestamp != null && timestamp > 0) {
            val epochSeconds = if (timestamp > 9_999_999_999L) {
                timestamp / 1000
            } else {
                timestamp
            }
            postTimeFormatter.format(
                Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault())
            )
        } else {
            normalized
        }
    }
    if (forumName.isBlank() && formattedTime.isBlank()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (forumName.isNotBlank()) {
            Row(
                modifier = Modifier
                    .widthIn(max = 220.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = MaterialTheme.shapes.small,
                    )
                    .clickable(
                        enabled = onForumClick != null,
                        onClick = { onForumClick?.invoke() },
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Topic,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = forumName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        if (formattedTime.isNotBlank()) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}
