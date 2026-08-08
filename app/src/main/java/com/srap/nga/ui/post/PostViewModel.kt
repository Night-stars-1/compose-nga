package com.srap.nga.ui.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
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
                                    it.result
                                } else {
                                    list + it.result
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
}
