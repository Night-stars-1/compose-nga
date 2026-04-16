package com.srap.nga.ui.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.button.ExpandableFab
import com.srap.nga.ui.component.button.ExpandableFabItem
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.modal.FavModal
import com.srap.nga.ui.component.modal.rememberFavState
import com.srap.nga.ui.component.post.PostReplyCard
import com.srap.nga.ui.component.post.PostContentCard
import com.srap.nga.utils.nga.HtmlUtil
import kotlinx.coroutines.flow.distinctUntilChanged

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
    var fabExpanded by remember { mutableStateOf(false) }
    var fabVisible by remember { mutableStateOf(true) }
    var favState = rememberFavState()
    val viewModel = hiltViewModel<PostViewModel, PostViewModel.ViewModelFactory>(key = id.toString()) { factory ->
        factory.create(id)
    }

    // 用于检测滚动方向的 ListState
    val listState = rememberLazyListState()

    // 监听滚动方向，下滑隐藏 FAB，上滑显示 FAB
    LaunchedEffect(listState) {
        snapshotFlow {
            val firstVisibleItem = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            // 返回是否正在向上滚动（内容向上移动，即手指向下划）
            firstVisibleItemScrollOffset > 0 || firstVisibleItem > 0
        }.distinctUntilChanged().collect { isScrollingDown ->
            // isScrollingDown 为 true 表示正在向下滚动（隐藏 FAB）
            fabVisible = !isScrollingDown
        }
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
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                ExpandableFab(
                    expanded = fabExpanded,
                    onExpandedChange = { fabExpanded = it },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    items = listOf(
                        ExpandableFabItem(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            label = "回复",
                            onClick = { /* TODO: 实现回复功能 */ }
                        ),
                        ExpandableFabItem(
                            icon = Icons.Default.Favorite,
                            label = "收藏",
                            onClick = { favState.open(id = id) }
                        ),
                        ExpandableFabItem(
                            icon = Icons.Default.Refresh,
                            label = "刷新",
                            onClick = { viewModel.refresh() }
                        )
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            RefreshLoadList(
                modifier = Modifier
                    .padding(innerPadding),
                viewModel = viewModel,
                listState = listState
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
                    // 去除帖子内容
                    if (index != 0) {
                        // 帖子评论
                        PostReplyCard(
                            avatar = item.author.avatar,
                            name = item.author.username,
                            time = item.postdate,
                            onAvatarClick = {
                                onUserInfo(item.author.uid)
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(bottom = 4.dp)
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
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = DividerDefaults.color.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // 右下角分页信息悬浮按钮
            SmallFloatingActionButton(
                onClick = { /* 可扩展为页码选择器 */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 88.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = "${viewModel.page}/${viewModel.totalPage}",
                    style = MaterialTheme.typography.labelMedium
                )
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
            modifier = Modifier
                .padding(horizontal = 8.dp),
            avatar = item.author.avatar,
            name = item.author.username,
            time = item.postdate,
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
                .padding(horizontal = 8.dp)
                .padding(bottom = 4.dp)
        ) {
            Text("共${viewModel.replyQuantity}个评论")
        }
    }
}
