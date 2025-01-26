package com.srap.nga.ui.component.userinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import com.srap.nga.utils.toHttps

/**
 * 用户信息卡片
 */
@Composable
fun UserInfoCard(
    avatar: String,
    name: String,
    description: String,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
    ) {
        val (avatarRef, nameRef, descriptionRef) = createRefs()
        // 头像
        AsyncImage(
            model = avatar.toHttps(),
            contentDescription = "头像",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start)
                }
        )

        // 名称
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 8.dp)
            }
        )

        // 注释
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(descriptionRef) {
                top.linkTo(nameRef.bottom, margin = 4.dp)
                start.linkTo(avatarRef.end, margin = 8.dp)
            }
        )
    }
}