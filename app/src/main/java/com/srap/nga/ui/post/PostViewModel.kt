package com.srap.nga.ui.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.PostVote
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.model.applyPostVoteResult
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

sealed interface PostContentState {
    data object Loading : PostContentState
    data object Content : PostContentState
    data object Error : PostContentState
}

@HiltViewModel(assistedFactory = PostViewModel.ViewModelFactory::class)
class PostViewModel @AssistedInject constructor(
    @Assisted("id") var id: Int,
    networkRepo: NetworkRepo,
    private val postVoteStore: PostVoteStore,
) : BaseRefreshLoadViewModel<PostResponse.Result>(networkRepo) {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(@Assisted("id") id: Int): PostViewModel
    }

    var response by mutableStateOf<PostResponse?>(null)

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

    private val pageByPid = mutableMapOf<Int, Int>()

    override fun fetchData() {
        val requestedPage = page
        viewModelScope.launch {
            networkRepo.getPost(id, requestedPage)
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
                            val userId = StorageUtils.Uid
                            val loadedPosts = loadedResponse.result.map { post ->
                                val persistedVote = postVoteStore.getVote(
                                    userId = userId,
                                    threadId = id,
                                    postId = post.pid,
                                )
                                val mergedPost = post.mergePostVoteState(persistedVote)
                                post.explicitVoteState?.let { explicitVote ->
                                    postVoteStore.setVote(
                                        userId = userId,
                                        threadId = id,
                                        postId = post.pid,
                                        vote = explicitVote,
                                    )
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

        pendingVotes = pendingVotes + (pid to value)
        viewModelScope.launch {
            try {
                networkRepo.votePost(
                    pid = pid,
                    value = value,
                    tid = id,
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

    fun resolvePostByPid(pid: Int, onResolved: (Int) -> Unit) {
        if (pid <= 0) return

        viewModelScope.launch {
            networkRepo.getPostByPid(pid).collect { state ->
                when (state) {
                    is LoadingState.Error -> ToastUtils.show(state.errMsg)
                    is LoadingState.Success -> {
                        val resolvedTid = state.response.result
                            .firstOrNull { it.tid > 0 }
                            ?.tid
                            ?.takeIf { it > 0 }
                            ?: state.response.tid.takeIf { it > 0 }
                        if (resolvedTid != null) {
                            onResolved(resolvedTid)
                        } else {
                            ToastUtils.show("未找到对应帖子")
                        }
                    }
                }
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
            threadId = id,
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
