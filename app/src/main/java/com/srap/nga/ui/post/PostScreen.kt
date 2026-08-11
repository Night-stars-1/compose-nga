package com.srap.nga.ui.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.model.PostVote
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.modal.FavModal
import com.srap.nga.ui.component.modal.rememberFavState
import com.srap.nga.ui.component.post.PostReplyCard
import com.srap.nga.ui.component.post.PostContentCard
import com.srap.nga.ui.component.post.PostLoadError
import com.srap.nga.ui.component.post.PostLoadingSkeleton
import com.srap.nga.utils.nga.HtmlUtil
import com.srap.nga.utils.StorageUtils

/**
 * 帖子详细页面
 * @param id 帖子id
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PostScreen(
    id: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    onUserInfo: (Int) -> Unit,
    isLoggedIn: Boolean,
    onViewLogin: () -> Unit,
    openUrl: (String) -> Unit,
) {
    val context = LocalContext.current
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val favState = rememberFavState()
    val viewModel = hiltViewModel<PostViewModel, PostViewModel.ViewModelFactory>(key = id.toString()) { factory ->
        factory.create(id)
    }
    val listState = rememberLazyListState(
        cacheWindow = LazyLayoutCacheWindow(
            ahead = 3200.dp,
            behind = 1600.dp,
        )
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val contentState = viewModel.contentState
    val postItems = viewModel.list
    val replyQuantity = viewModel.replyQuantity
    val onViewPostByPid: (Int) -> Unit = { pid ->
        viewModel.resolvePostByPid(pid, onViewPost)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
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
                            contentDescription = "帖子操作",
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
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                dropdownMenuExpanded = false
                                favState.open(id = id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("分享") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                dropdownMenuExpanded = false
                                context.sharePost(id)
                            },
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        RefreshLoadList(
            modifier = Modifier
                .padding(innerPadding),
            viewModel = viewModel,
            listState = listState,
            showRefreshIndicator = contentState != PostContentState.Loading,
            initialLoadAsRefresh = false,
        ) {
            when (contentState) {
                PostContentState.Loading -> {
                    item(key = "post-loading-skeleton") {
                        PostLoadingSkeleton()
                    }
                }
                PostContentState.Error -> {
                    item(key = "post-load-error") {
                        PostLoadError(onRetry = viewModel::retryInitialLoad)
                    }
                }
                PostContentState.Content -> {
                    val post = postItems.firstOrNull()
                    if (post != null) {
                        item(key = "post-header") {
                            PostHeader(
                                item = post,
                                forumId = viewModel.response?.fid ?: 0,
                                forumName = viewModel.response?.forumName.orEmpty(),
                                replyQuantity = replyQuantity,
                                onViewPost = onViewPost,
                                onForumClick = onViewTopicSubject,
                                onUserInfo = onUserInfo,
                                openUrl = openUrl,
                                onViewPostByPid = onViewPostByPid,
                                isFollowing = viewModel.authorFollow != 0,
                                followLoading = viewModel.isFollowLoading,
                                onFollowClick = if (StorageUtils.Uid != 0 && post.author.uid != StorageUtils.Uid) {
                                    viewModel::toggleAuthorFollow
                                } else null,
                                pendingVote = viewModel.pendingVotes[post.pid],
                                onLikeClick = {
                                    if (isLoggedIn) {
                                        viewModel.togglePostVote(post.pid, PostVote.LIKE)
                                    } else {
                                        onViewLogin()
                                    }
                                },
                                onDislikeClick = {
                                    if (isLoggedIn) {
                                        viewModel.togglePostVote(post.pid, PostVote.DISLIKE)
                                    } else {
                                        onViewLogin()
                                    }
                                },
                            )
                        }

                        items(
                            items = postItems.drop(1),
                            key = { item -> item.pid },
                        ) { item ->
                            PostReplyCard(
                                avatar = item.author.avatar,
                                name = item.author.username,
                                group = item.author.group.orEmpty(),
                                rvrc = item.author.rvrc.orEmpty(),
                                posts = item.author.posts ?: 0,
                                medals = item.author.medal.orEmpty(),
                                postDate = item.postDate,
                                likeCount = item.likeCount,
                                voteState = item.voteState,
                                pendingVote = viewModel.pendingVotes[item.pid],
                                onLikeClick = {
                                    if (isLoggedIn) {
                                        viewModel.togglePostVote(item.pid, PostVote.LIKE)
                                    } else {
                                        onViewLogin()
                                    }
                                },
                                onDislikeClick = {
                                    if (isLoggedIn) {
                                        viewModel.togglePostVote(item.pid, PostVote.DISLIKE)
                                    } else {
                                        onViewLogin()
                                    }
                                },
                                onAvatarClick = {
                                    onUserInfo(item.author.uid)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                HtmlUtil.FromHtml(
                                    item.content,
                                    uid = item.author.uid.toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    onViewPost = onViewPost,
                                    openUrl = openUrl,
                                    onViewPostByPid = onViewPostByPid,
                                )
                            }
                        }
                    } else {
                        item(key = "post-empty-error") {
                            PostLoadError(onRetry = viewModel::retryInitialLoad)
                        }
                    }
                }
            }
        }

        FavModal(state = favState)
    }
}

@Composable
fun PostHeader(
    item: PostResponse.Result,
    forumId: Int,
    forumName: String,
    replyQuantity: Int,
    onViewPost: (Int) -> Unit,
    onForumClick: (Int, Boolean?) -> Unit,
    onUserInfo: (Int) -> Unit,
    openUrl: (String) -> Unit,
    onViewPostByPid: ((Int) -> Unit)? = null,
    isFollowing: Boolean = item.follow != 0,
    followLoading: Boolean = false,
    onFollowClick: (() -> Unit)? = null,
    pendingVote: Int? = null,
    onLikeClick: (() -> Unit)? = null,
    onDislikeClick: (() -> Unit)? = null,
) {
    PostContentCard(
        modifier = Modifier.fillMaxWidth(),
        avatar = item.author.avatar,
        name = item.author.username,
        onAvatarClick = {
            onUserInfo(item.author.uid)
        },
        isFollowing = isFollowing,
        followLoading = followLoading,
        onFollowClick = onFollowClick,
        group = item.author.group.orEmpty(),
        rvrc = item.author.rvrc.orEmpty(),
        posts = item.author.posts ?: 0,
        medals = item.author.medal.orEmpty(),
        forumName = forumName,
        onForumClick = if (forumId > 0) {
            { onForumClick(forumId, null) }
        } else null,
        postDate = item.postDate,
        likeCount = item.likeCount,
        voteState = item.voteState,
        pendingVote = pendingVote,
        onLikeClick = onLikeClick,
        onDislikeClick = onDislikeClick,
    ) {
        HtmlUtil.FromHtml(
            item.content,
            uid = item.author.uid.toString(),
            modifier = Modifier.fillMaxWidth(),
            onViewPost = onViewPost,
            openUrl = openUrl,
            onViewPostByPid = onViewPostByPid,
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "评论",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "$replyQuantity 条",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
