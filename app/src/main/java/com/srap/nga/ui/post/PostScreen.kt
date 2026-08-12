package com.srap.nga.ui.post

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.model.PostVote
import com.srap.nga.logic.preferences.AppPreferences
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.modal.FavModal
import com.srap.nga.ui.component.modal.rememberFavState
import com.srap.nga.ui.component.post.CommentBbCodeToolbar
import com.srap.nga.ui.component.post.EmojiPickerPanel
import com.srap.nga.ui.component.post.insertSnippet
import com.srap.nga.ui.component.post.PostReplyCard
import com.srap.nga.ui.component.post.PostContentCard
import com.srap.nga.ui.component.post.PostLoadError
import com.srap.nga.ui.component.post.PostLoadingSkeleton
import com.srap.nga.utils.nga.HtmlUtil
import com.srap.nga.utils.StorageUtils

/**
 * 帖子详细页面
 * @param id 主题 ID
 * @param pid 回复 ID，为 0 时按主题 ID 加载
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PostScreen(
    id: Int,
    pid: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
    onViewPostByPid: (Int) -> Unit,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    onUserInfo: (Int) -> Unit,
    isLoggedIn: Boolean,
    onViewLogin: () -> Unit,
    openUrl: (String) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val commentFocusRequester = remember { FocusRequester() }
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    val favState = rememberFavState()
    val viewModelKey = if (pid > 0) "pid-$pid" else "tid-$id"
    val viewModel = hiltViewModel<PostViewModel, PostViewModel.ViewModelFactory>(key = viewModelKey) { factory ->
        factory.create(id, pid)
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
    val threadId = viewModel.threadId
    val commentOwner = StorageUtils.Uid.takeIf { isLoggedIn } ?: 0
    var commentText by rememberSaveable(id, pid, commentOwner, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var commentBarVisible by rememberSaveable(id, pid, commentOwner) { mutableStateOf(false) }
    var emojiPanelVisible by rememberSaveable(id, pid, commentOwner) { mutableStateOf(false) }
    var replyTarget by remember(id, pid, commentOwner) {
        mutableStateOf<PostResponse.Result?>(null)
    }

    LaunchedEffect(commentBarVisible, replyTarget?.pid, contentState, isLoggedIn) {
        if (commentBarVisible && isLoggedIn && contentState == PostContentState.Content) {
            commentFocusRequester.requestFocus()
        }
    }

    BackHandler(enabled = commentBarVisible) {
        if (emojiPanelVisible) {
            emojiPanelVisible = false
        } else {
            commentBarVisible = false
            replyTarget = null
            focusManager.clearFocus()
        }
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
                        enabled = threadId > 0,
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
                            enabled = threadId > 0,
                            onClick = {
                                dropdownMenuExpanded = false
                                favState.open(id = threadId)
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
                            enabled = threadId > 0,
                            onClick = {
                                dropdownMenuExpanded = false
                                context.sharePost(threadId)
                            },
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = threadId > 0 && contentState == PostContentState.Content && !commentBarVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        if (isLoggedIn) {
                            replyTarget = null
                            commentBarVisible = true
                        } else {
                            onViewLogin()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = if (isLoggedIn) "发表评论" else "登录后发表评论",
                    )
                }
            }
        },
        bottomBar = {
            if (threadId > 0 && commentBarVisible) {
                PostCommentBar(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = replyTarget?.author?.username?.let { "回复 $it" }
                        ?: "发表评论",
                    isLoggedIn = isLoggedIn,
                    isSending = viewModel.isCommentSending,
                    enabled = contentState == PostContentState.Content,
                    focusRequester = commentFocusRequester,
                    emojiPanelVisible = emojiPanelVisible,
                    onToggleEmojiPanel = {
                        emojiPanelVisible = !emojiPanelVisible
                        if (emojiPanelVisible) {
                            // 打开表情面板时收起输入法，避免面板和键盘叠加。
                            focusManager.clearFocus()
                        }
                    },
                    onInputFocused = { emojiPanelVisible = false },
                    onSend = {
                        val content = commentText.text.trim()
                        if (!isLoggedIn) {
                            onViewLogin()
                        } else if (content.isNotBlank()) {
                            viewModel.submitComment(content, replyTarget) {
                                commentText = TextFieldValue()
                                replyTarget = null
                                commentBarVisible = false
                                emojiPanelVisible = false
                                focusManager.clearFocus()
                            }
                        }
                    },
                )
            }
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
                                onReplyClick = if (item.pid > 0 && item.lou > 0) {
                                    {
                                        if (isLoggedIn) {
                                            replyTarget = item
                                            commentBarVisible = true
                                        } else {
                                            onViewLogin()
                                        }
                                    }
                                } else null,
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

        if (commentBarVisible) {
            // 覆盖帖子内容区域的透明层：点击输入栏以外的地方收起回复框。
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            commentBarVisible = false
                            replyTarget = null
                            emojiPanelVisible = false
                            focusManager.clearFocus()
                        }
                    },
            )
        }

        FavModal(state = favState)
    }
}

/** 帖子页面底部的评论输入栏，支持 NGA BBCode 标签和表情输入。 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PostCommentBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isLoggedIn: Boolean,
    isSending: Boolean,
    enabled: Boolean,
    focusRequester: FocusRequester,
    emojiPanelVisible: Boolean,
    onToggleEmojiPanel: () -> Unit,
    onInputFocused: () -> Unit,
    onSend: () -> Unit,
) {
    val canSend = enabled && !isSending && (value.text.isNotBlank() || !isLoggedIn)
    // 展开后编辑区占满整个屏幕，方便撰写长回复；是否默认全屏由设置项控制。
    val defaultFullscreenInput by AppPreferences.defaultFullscreenInput.collectAsState()
    var inputExpanded by rememberSaveable { mutableStateOf(isLoggedIn && defaultFullscreenInput) }
    // 全屏模式下可以切换到渲染预览，检查 BBCode 的实际效果。
    var previewVisible by rememberSaveable { mutableStateOf(false) }
    val isPreviewing = inputExpanded && previewVisible

    BackHandler(enabled = inputExpanded) {
        if (previewVisible) {
            previewVisible = false
        } else {
            inputExpanded = false
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (inputExpanded) {
                    Modifier
                        .fillMaxHeight()
                        .statusBarsPadding()
                } else {
                    Modifier
                }
            )
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (inputExpanded) Modifier.fillMaxHeight() else Modifier),
        ) {
            if (isLoggedIn) {
                CommentBbCodeToolbar(
                    value = value,
                    onValueChange = onValueChange,
                    emojiPanelVisible = emojiPanelVisible,
                    onToggleEmojiPanel = onToggleEmojiPanel,
                    enabled = enabled && !isSending && !isPreviewing,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .let { if (inputExpanded) it.weight(1f) else it },
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(if (inputExpanded) Modifier.fillMaxHeight() else Modifier),
                ) {
                    if (isPreviewing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(24.dp),
                                )
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            if (value.text.isBlank()) {
                                Text(
                                    text = "暂无内容",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                val previewHtml = remember(value.text) {
                                    value.text.replace("\n", "<br/>")
                                }
                                HtmlUtil.FromHtml(
                                    previewHtml,
                                    uid = StorageUtils.Uid.toString(),
                                    modifier = Modifier.fillMaxWidth(),
                                    onViewPost = {},
                                    openUrl = {},
                                )
                            }
                        }
                    } else {
                        TextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (inputExpanded) Modifier.fillMaxHeight() else Modifier
                                )
                                .focusRequester(focusRequester)
                                .onFocusChanged { state ->
                                    if (state.isFocused) {
                                        onInputFocused()
                                    }
                                },
                            enabled = enabled && !isSending,
                            readOnly = !isLoggedIn,
                            placeholder = {
                                Text(if (isLoggedIn) placeholder else "登录后发表评论")
                            },
                            minLines = if (isLoggedIn) 3 else 1,
                            maxLines = if (inputExpanded) Int.MAX_VALUE else 8,
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                        )
                    }
                    if (!isLoggedIn) {
                        // 输入控件会消费点击事件，使用覆盖层保证整块区域都能进入登录页。
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(enabled = enabled, onClick = onSend),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoggedIn && inputExpanded) {
                        IconToggleButton(
                            checked = previewVisible,
                            onCheckedChange = { previewVisible = it },
                            enabled = enabled && !isSending,
                            shapes = IconButtonDefaults.toggleableShapes(),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = if (previewVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (previewVisible) "返回编辑" else "预览效果",
                            )
                        }
                    }
                    if (isLoggedIn) {
                        IconToggleButton(
                            checked = inputExpanded,
                            onCheckedChange = { expanded ->
                                inputExpanded = expanded
                                if (!expanded) {
                                    previewVisible = false
                                }
                            },
                            enabled = enabled && !isSending,
                            shapes = IconButtonDefaults.toggleableShapes(),
                            modifier = Modifier.size(48.dp),
                        ) {
                            Icon(
                                imageVector = if (inputExpanded) {
                                    Icons.Default.FullscreenExit
                                } else {
                                    Icons.Default.Fullscreen
                                },
                                contentDescription = if (inputExpanded) "收起编辑框" else "展开编辑框",
                            )
                        }
                    }
                    FilledIconButton(
                        onClick = onSend,
                        enabled = canSend,
                        shapes = IconButtonDefaults.shapes(),
                        modifier = Modifier.size(48.dp),
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = if (isLoggedIn) "发送评论" else "登录后发表评论",
                            )
                        }
                    }
                }
            }
            if (isLoggedIn) {
                AnimatedVisibility(visible = emojiPanelVisible) {
                    EmojiPickerPanel(
                        onEmojiSelected = { code ->
                            onValueChange(value.insertSnippet(code))
                        },
                    )
                }
            }
        }
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
