package com.srap.nga.ui.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.PostVote
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.model.applyPostVoteResult
import com.srap.nga.logic.model.buildNgaQuoteContent
import com.srap.nga.logic.model.containingThreadId
import com.srap.nga.logic.model.explicitVoteState
import com.srap.nga.logic.model.mergePostVoteState
import com.srap.nga.logic.preferences.PostVoteStore
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.StorageUtils
import com.srap.nga.utils.ToastUtils
import com.srap.nga.utils.nga.HtmlUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

sealed interface PostContentState {
    data object Loading : PostContentState
    data object Content : PostContentState
    data object Error : PostContentState
}

@HiltViewModel(assistedFactory = PostViewModel.ViewModelFactory::class)
class PostViewModel @AssistedInject constructor(
    @Assisted("id") initialThreadId: Int,
    @Assisted("pid") private val initialPid: Int,
    networkRepo: NetworkRepo,
    private val postVoteStore: PostVoteStore,
) : BaseRefreshLoadViewModel<PostResponse.Result>(networkRepo) {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(
            @Assisted("id") id: Int,
            @Assisted("pid") pid: Int,
        ): PostViewModel
    }

    var response by mutableStateOf<PostResponse?>(null)

    var threadId by mutableIntStateOf(initialThreadId)
        private set

    var contentState by mutableStateOf<PostContentState>(PostContentState.Loading)
        private set

    var replyQuantity by mutableIntStateOf(0)
        private set

    var isFollowLoading by mutableStateOf(false)
        private set

    var authorFollow by mutableIntStateOf(0)
        private set

    var pendingVotes by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** 是否正在提交评论。 */
    var isCommentSending by mutableStateOf(false)
        private set

    private val pageByPid = mutableMapOf<Int, Int>()
    private var refreshAfterCurrentLoad = false

    override fun fetchData() {
        val requestedPage = page
        viewModelScope.launch {
            // 首次通过 pid 打开时需要定位主题；定位成功后刷新直接使用主题接口，
            // 这样发表评论后可以拿到完整的最新主题和回复列表。
            val request = if (initialPid > 0 && threadId <= 0) {
                networkRepo.getPostByPid(initialPid, requestedPage)
            } else {
                networkRepo.getPost(threadId, requestedPage)
            }
            request
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                            if (requestedPage == 1 && list.isEmpty()) {
                                contentState = PostContentState.Error
                            }
                        }
                        is LoadingState.Success -> {
                            val loadedResponse = state.response
                            loadedResponse.containingThreadId?.let { resolvedThreadId ->
                                threadId = resolvedThreadId
                            }
                            val userId = StorageUtils.Uid
                            val voteThreadId = threadId.takeIf { it > 0 }
                            val loadedPosts = loadedResponse.result.map { post ->
                                val persistedVote = voteThreadId?.let { id ->
                                    postVoteStore.getVote(
                                        userId = userId,
                                        threadId = id,
                                        postId = post.pid,
                                    )
                                }
                                val mergedPost = post.mergePostVoteState(persistedVote)
                                if (voteThreadId != null) {
                                    post.explicitVoteState?.let { explicitVote ->
                                        postVoteStore.setVote(
                                            userId = userId,
                                            threadId = voteThreadId,
                                            postId = post.pid,
                                            vote = explicitVote,
                                        )
                                    }
                                }
                                mergedPost
                            }
                            val sourcePage = loadedResponse.currentPage.takeIf { it > 0 }
                                ?: requestedPage
                            if (requestedPage == 1) {
                                pageByPid.clear()
                            }
                            loadedPosts.forEach { post ->
                                pageByPid[post.pid] = sourcePage
                            }
                            val isContentReady = if (requestedPage == 1) {
                                loadedResponse.result.firstOrNull()?.let { firstPost ->
                                    try {
                                        HtmlUtil.preload(firstPost.content)
                                        true
                                    } catch (_: Exception) {
                                        ToastUtils.show("正文解析失败")
                                        false
                                    }
                                } ?: false
                            } else {
                                true
                            }

                            response = loadedResponse
                            response?.let {
                                list = if (requestedPage == 1) {
                                    authorFollow = it.result.firstOrNull()?.follow ?: 0
                                    loadedPosts
                                } else {
                                    list + loadedPosts
                                }
                                page = it.currentPage
                                totalPage = it.totalPage
                                replyQuantity = it.vrows - 1
                                if (requestedPage == 1) {
                                    contentState = if (isContentReady) {
                                        PostContentState.Content
                                    } else {
                                        PostContentState.Error
                                    }
                                }
                            }
                        }
                    }
                    super.fetchData()
                    if (refreshAfterCurrentLoad) {
                        refreshAfterCurrentLoad = false
                        refresh()
                    }
                }
            }
        }

    override fun refresh() {
        if (!isRefreshing && !isLoadMore) {
            page = 1
            isLoadMore = false
            isRefreshing = true
            fetchData()
        }
    }

    fun retryInitialLoad() {
        if (!isRefreshing && !isLoadMore) {
            contentState = PostContentState.Loading
            isLoaded = false
            refresh()
        }
    }

    /** 提交主题评论，指定回复目标时使用评论引用接口。 */
    fun submitComment(
        content: String,
        replyTo: PostResponse.Result? = null,
        onSuccess: () -> Unit = {},
    ) {
        val normalizedContent = content.trim()
        if (normalizedContent.isBlank() || isCommentSending) return

        val forumId = response?.fid ?: 0
        val targetThreadId = threadId
        if (forumId == 0 || targetThreadId <= 0) {
            ToastUtils.show("帖子信息尚未加载完成")
            return
        }
        if (replyTo != null && (replyTo.pid <= 0 || replyTo.lou <= 0)) {
            ToastUtils.show("评论信息不完整，暂时无法回复")
            return
        }

        isCommentSending = true
        viewModelScope.launch {
            try {
                val submitState = if (replyTo == null) {
                    networkRepo.submitComment(
                        fid = forumId,
                        tid = targetThreadId,
                        content = normalizedContent,
                    ).first()
                } else {
                    networkRepo.submitQuote(
                        fid = forumId,
                        tid = targetThreadId,
                        pid = replyTo.pid,
                        content = buildNgaQuoteContent(
                            post = replyTo,
                            threadId = targetThreadId,
                            replyContent = normalizedContent,
                        ),
                    ).first()
                }
                when (submitState) {
                    is LoadingState.Error -> ToastUtils.show(submitState.errMsg)
                    is LoadingState.Success -> {
                        ToastUtils.show(
                            submitState.response.resultMessage?.takeIf { it.isNotBlank() }
                                ?: if (replyTo == null) "评论成功" else "回复成功"
                        )
                        onSuccess()
                        refreshAfterComment()
                    }
                }
            } finally {
                isCommentSending = false
            }
        }
    }

    /** 当前列表请求结束后再刷新，避免评论成功时与下拉刷新或加载更多互相覆盖。 */
    private fun refreshAfterComment() {
        if (isRefreshing || isLoadMore) {
            refreshAfterCurrentLoad = true
        } else {
            refresh()
        }
    }

    fun toggleAuthorFollow() {
        val post = list.firstOrNull() ?: return
        val author = post.author
        if (isFollowLoading) return

        val previousFollow = authorFollow
        val shouldFollow = previousFollow == 0
        val targetFollow = if (shouldFollow) 1 else 0
        isFollowLoading = true
        authorFollow = targetFollow
        updateAuthorFollow(targetFollow)

        viewModelScope.launch {
            val request = if (shouldFollow) {
                networkRepo.followUser(author.uid)
            } else {
                networkRepo.unfollowUser(author.uid)
            }
            request.collect { state ->
                when (state) {
                    is LoadingState.Error -> {
                        val alreadyInTargetState = if (shouldFollow) {
                            state.errMsg.contains("已经关注") || state.errMsg.contains("已关注")
                        } else {
                            state.errMsg.contains("未关注") || state.errMsg.contains("尚未关注")
                        }
                        if (alreadyInTargetState) {
                            authorFollow = targetFollow
                            updateAuthorFollow(targetFollow)
                        } else {
                            authorFollow = previousFollow
                            updateAuthorFollow(previousFollow)
                        }
                        ToastUtils.show(state.errMsg)
                    }
                    is LoadingState.Success -> {
                        ToastUtils.show(if (shouldFollow) "关注成功" else "已取消关注")
                    }
                }
                isFollowLoading = false
            }
        }
    }

    fun togglePostVote(pid: Int, value: Int) {
        if (value != PostVote.LIKE && value != PostVote.DISLIKE) return
        if (pendingVotes.containsKey(pid)) return
        val atPage = pageByPid[pid] ?: return
        val userId = StorageUtils.Uid
        if (userId <= 0) return
        val targetThreadId = threadId.takeIf { it > 0 } ?: return

        pendingVotes = pendingVotes + (pid to value)
        viewModelScope.launch {
            try {
                networkRepo.votePost(
                    pid = pid,
                    value = value,
                    tid = targetThreadId,
                    atPage = atPage,
                ).collect { state ->
                    when (state) {
                        is LoadingState.Error -> ToastUtils.show(state.errMsg)
                        is LoadingState.Success -> {
                            val lastVote = state.response.lastState
                            if (lastVote == null) {
                                ToastUtils.show("投票结果异常，请刷新后确认")
                                return@collect
                            }
                            updateReplyVote(
                                pid = pid,
                                requestedVote = value,
                                lastVote = lastVote,
                                userId = userId,
                            )
                        }
                    }
                }
            } finally {
                pendingVotes = pendingVotes - pid
            }
        }
    }

    private fun updateAuthorFollow(follow: Int) {
        list = list.mapIndexed { index, post ->
            if (index == 0) {
                post.copy(follow = follow)
            } else {
                post
            }
        }
    }

    private fun updateReplyVote(
        pid: Int,
        requestedVote: Int,
        lastVote: Int,
        userId: Int,
    ) {
        postVoteStore.setVote(
            userId = userId,
            threadId = threadId,
            postId = pid,
            vote = PostVote.nextState(requestedVote, lastVote),
        )
        list = list.map { post ->
            if (post.pid == pid) {
                post.applyPostVoteResult(
                    requestedVote = requestedVote,
                    lastVote = lastVote,
                )
            } else {
                post
            }
        }
    }
}
