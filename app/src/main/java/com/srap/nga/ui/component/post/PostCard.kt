package com.srap.nga.ui.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import com.srap.nga.utils.toHttps

/**
 * 帖子内容卡片
 */
@Composable
fun PostContentCard(
    avatar: String,
    name: String,
    time: String = "",
    likeCount: Int = 0,
    dislikeCount: Int = 0,
    replyCount: Int = 0,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onDislikeClick: () -> Unit = {},
    onReplyClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    message: @Composable () -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (avatarRef, nameRef, expandRef, messageRef, actionBarRef) = createRefs()

        // 头像
        AsyncImage(
            model = avatar.toHttps(),
            contentDescription = "头像",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start)
                }
                .clickable {
                    onAvatarClick()
                },
        )

        // 名称和时间
        Row(
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 4.dp)
            }
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (time.isNotEmpty()) {
                Text(
                    text = " · $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 操作栏
        Box(
            modifier = Modifier
                .aspectRatio(1f, false)
                .constrainAs(expandRef) {
                    top.linkTo(avatarRef.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(avatarRef.bottom)
                    height = Dimension.fillToConstraints
                }
        ) {
            IconButton(
                onClick = {
                    dropdownMenuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            DropdownMenu(
                expanded = dropdownMenuExpanded,
                onDismissRequest = {
                    dropdownMenuExpanded = false
                },
            ) {
                listOf("操作1", "操作2").forEachIndexed { index, menu ->
                    DropdownMenuItem(
                        text = { Text(menu) },
                        onClick = {
                            dropdownMenuExpanded = false
                        }
                    )
                }
            }

        }

        // 消息
        Box(
            modifier = Modifier.constrainAs(messageRef) {
                top.linkTo(avatarRef.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        ) {
            message()
        }

        // 操作栏 (点赞、点踩、回复、更多)
        Row(
            modifier = Modifier
                .constrainAs(actionBarRef) {
                    top.linkTo(messageRef.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧: 点赞 + 点赞数 + 点踩 + 点踩数
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "点赞",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    if (likeCount > 0) {
                        Text(
                            text = "$likeCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDislikeClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "点踩",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    if (dislikeCount > 0) {
                        Text(
                            text = "$dislikeCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            // 右侧: 回复 + 回复数 + 更多
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onReplyClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "回复",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    if (replyCount > 0) {
                        Text(
                            text = "$replyCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "更多",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onMoreClick() },
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 操作栏下方的分割线
        HorizontalDivider(
            modifier = Modifier.constrainAs(createRef()) {
                top.linkTo(actionBarRef.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
        )
    }
}

/**
 * 评论卡片
 */
@Composable
fun PostReplyCard(
    avatar: String,
    name: String,
    time: String = "",
    likeCount: Int = 0,
    dislikeCount: Int = 0,
    replyCount: Int = 0,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onDislikeClick: () -> Unit = {},
    onReplyClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    message: @Composable () -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (avatarRef, nameRef, expandRef, messageRef, actionBarRef) = createRefs()

        // 头像
        AsyncImage(
            model = avatar.toHttps(),
            contentDescription = "头像",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start)
                }
                .clickable {
                    onAvatarClick()
                },
        )

        // 名称和时间
        Row(
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 4.dp)
            }
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (time.isNotEmpty()) {
                Text(
                    text = " · $time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 操作栏
        Box(
            modifier = Modifier
                .aspectRatio(1f, false)
                .constrainAs(expandRef) {
                    top.linkTo(avatarRef.top)
                    bottom.linkTo(avatarRef.bottom)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
        ) {
            IconButton(
                onClick = {
                    dropdownMenuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            DropdownMenu(
                expanded = dropdownMenuExpanded,
                onDismissRequest = {
                    dropdownMenuExpanded = false
                },
            ) {
                listOf("操作1", "操作2").forEachIndexed { index, menu ->
                    DropdownMenuItem(
                        text = { Text(menu) },
                        onClick = {
                            dropdownMenuExpanded = false
                        }
                    )
                }
            }
        }

        // 消息
        Box(
            modifier = Modifier.constrainAs(messageRef) {
                top.linkTo(avatarRef.bottom, margin = 4.dp)
                start.linkTo(nameRef.start)
                end.linkTo(parent.end, margin = 4.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        ) {
            message()
        }

        // 操作栏上方的分割线
        HorizontalDivider(
            modifier = Modifier.constrainAs(createRef()) {
                top.linkTo(messageRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 1f)
        )

        // 操作栏 (点赞、点踩、回复、更多)
        Row(
            modifier = Modifier
                .constrainAs(actionBarRef) {
                    top.linkTo(messageRef.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧: 点赞 + 点赞数 + 点踩 + 点踩数
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "点赞",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    if (likeCount > 0) {
                        Text(
                            text = "$likeCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDislikeClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = "点踩",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    if (dislikeCount > 0) {
                        Text(
                            text = "$dislikeCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            // 右侧: 回复 + 回复数 + 更多
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onReplyClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "回复",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    if (replyCount > 0) {
                        Text(
                            text = "$replyCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "更多",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onMoreClick() },
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}