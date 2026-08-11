package com.srap.nga.ui.userinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.srap.nga.logic.model.UserInfoResponse
import com.srap.nga.ui.component.UserAvatar

@Composable
fun MyProfileHeader(
    result: UserInfoResponse.Result,
    modifier: Modifier = Modifier,
) {
    val role = listOf(result.title, result.group)
        .filter(String::isNotBlank)
        .distinct()
        .joinToString(" · ")
    val details = listOfNotNull(
        result.ipLoc.takeIf(String::isNotBlank)?.let { "IP 属地 $it" },
        "UID ${result.uid}",
    ).joinToString(" · ")

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                UserAvatar(
                    avatar = result.avatar,
                    name = result.username,
                    modifier = Modifier.size(64.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.username,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (role.isNotBlank()) {
                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (result.signature.isNotBlank()) {
                        Text(
                            text = result.signature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ProfileMetric(
                    label = "帖子",
                    value = result.posts.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProfileMetric(
                    label = "获赞",
                    value = result.countLike.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProfileMetric(
                    label = "粉丝",
                    value = result.followByNum.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProfileMetric(
                    label = "威望",
                    value = result.rvrc,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun MyProfileHeaderPlaceholder(
    uid: Int,
    name: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val displayName = name?.takeIf(String::isNotBlank) ?: "用户 $uid"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 188.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(
                    avatar = "",
                    name = displayName,
                    modifier = Modifier.size(64.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "UID $uid",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("帖子", "获赞", "粉丝", "威望").forEach { label ->
                    ProfileMetric(
                        label = label,
                        value = "-",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
