package com.srap.nga.ui.component.userinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.srap.nga.ui.component.UserAvatar

/**
 * 用户信息卡片
 */
@Composable
fun UserInfoCard(
    avatar: String,
    name: String,
    description: String,
    isFollowing: Boolean = false,
    followLoading: Boolean = false,
    onFollowClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(8.dp)
    ) {
        val (avatarRef, nameRef, descriptionRef, followRef) = createRefs()
        // 头像
        UserAvatar(
            avatar = avatar,
            name = name,
            modifier = Modifier
                .size(48.dp)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start)
                }
        )

        // 名称
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 8.dp)
                if (onFollowClick != null) {
                    end.linkTo(followRef.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
            }
        )

        // 注释
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(descriptionRef) {
                top.linkTo(nameRef.bottom, margin = 4.dp)
                start.linkTo(avatarRef.end, margin = 8.dp)
                if (onFollowClick != null) {
                    end.linkTo(followRef.start, margin = 8.dp)
                } else {
                    end.linkTo(parent.end)
                }
                width = Dimension.fillToConstraints
            }
        )

        if (onFollowClick != null) {
            FilledTonalButton(
                onClick = onFollowClick,
                enabled = !followLoading,
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.constrainAs(followRef) {
                    top.linkTo(avatarRef.top)
                    bottom.linkTo(avatarRef.bottom)
                    end.linkTo(parent.end)
                },
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
