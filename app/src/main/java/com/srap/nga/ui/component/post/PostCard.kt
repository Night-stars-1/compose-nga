package com.srap.nga.ui.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage

@Composable
fun PostCard(
    avatar: String,
    name: String,
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {},
    message: @Composable () -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    return ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (avatarRef, nameRef, expandRef, messageRef) = createRefs()

        // 头像
        AsyncImage(
            model = avatar,
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

        // 名称
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 4.dp)
            }
        )

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
                bottom.linkTo(parent.bottom, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            message()
        }
    }
}