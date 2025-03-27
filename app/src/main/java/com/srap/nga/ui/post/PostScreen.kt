package com.srap.nga.ui.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.modal.FavModal
import com.srap.nga.ui.component.modal.ModalBottomActionSheet
import com.srap.nga.ui.component.modal.rememberFavState
import com.srap.nga.ui.component.post.PostReplyCard
import com.srap.nga.ui.component.post.PostContentCard
import com.srap.nga.utils.nga.HtmlUtil
import kotlinx.coroutines.launch

/**
 * 帖子详细页面
 * @param id 帖子id
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    id: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
    onUserInfo: (Int) -> Unit,
    openUrl: (String) -> Unit,
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    var favState = rememberFavState()
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            dropdownMenuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownMenuExpanded,
                        onDismissRequest = {
                            dropdownMenuExpanded = false
                        },
                    ) {
                        DropdownMenuItem(
                            text = { Text("收藏") },
                            onClick = {
                                dropdownMenuExpanded = false
                                favState.open(id = id)
                            }
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        RefreshLoadList(
            modifier = Modifier
                .padding(innerPadding),
            viewModel = viewModel
        ) {
            item {
                PostHeader(
                    viewModel = viewModel,
                    onViewPost = onViewPost,
                    onUserInfo = onUserInfo,
                    openUrl = openUrl,
                )
            }

            itemsIndexed(viewModel.list) { index, item ->
                // 帖子评论
                PostReplyCard(
                    avatar = item.author.avatar,
                    name = item.author.username,
                    onAvatarClick = {
                        onUserInfo(item.author.uid)
                    }
                ) {
                    HtmlUtil.FromHtml(
                        item.content,
                        uid = item.author.uid.toString(),
                        modifier = Modifier
                            .fillMaxSize(),
                        onViewPost = onViewPost,
                        openUrl = openUrl,
                    )
                }
            }
        }

        FavModal(state = favState)
    }
}

@Composable
fun PostHeader(
    viewModel: PostViewModel,
    onViewPost: (Int) -> Unit,
    onUserInfo: (Int) -> Unit,
    openUrl: (String) -> Unit,
) {
    if (viewModel.list.isNotEmpty()) {
        val item = viewModel.list[0]
        // 帖子内容
        PostContentCard(
            avatar = item.author.avatar,
            name = item.author.username,
            onAvatarClick = {
                onUserInfo(item.author.uid)
            }
        ) {
            HtmlUtil.FromHtml(
                item.content,
                uid = item.author.uid.toString(),
                modifier = Modifier
                    .fillMaxSize(),
                onViewPost = onViewPost,
                openUrl = openUrl
            )
        }

//                Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            thickness = 1.dp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Text("共${viewModel.replyQuantity}个评论")
        }
    }
}
