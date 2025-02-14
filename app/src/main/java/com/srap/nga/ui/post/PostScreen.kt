package com.srap.nga.ui.post

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule.NGA_ATTACHMENTS_URL
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.post.PostCard
import com.srap.nga.utils.nga.HtmlUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    id: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
    onUserInfo: (Int) -> Unit
) {
    val viewModel = hiltViewModel<PostViewModel, PostViewModel.ViewModelFactory>(key = id.toString()) { factory ->
        factory.create(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                title = {
                    Text(
                        text = viewModel.response?.tsubject ?: "主题",
                        maxLines = 1
                    )
                }
            )
        },
    ) { innerPadding ->
        RefreshLoadList(
            modifier = Modifier
                .padding(innerPadding),
            viewModel = viewModel,
        ) { index, item ->
            // 帖子内容，或评论
            PostCard(
                avatar = item.author.avatar,
                name = item.author.username,
                onAvatarClick = {
                    onUserInfo(item.author.uid)
                }
            ) {
                HtmlUtil.FromHtml(
                    item.content,
                    uid = item.author.uid.toString(),
                    images = item.attches?.map { NGA_ATTACHMENTS_URL.format(it.attachUrl) } ?: emptyList<String>(),
                    modifier = Modifier
                        .fillMaxSize(),
                    onViewPost = onViewPost,
                )
            }
            if (index != viewModel.list.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    thickness = 1.dp
                )
            }
        }
    }
}