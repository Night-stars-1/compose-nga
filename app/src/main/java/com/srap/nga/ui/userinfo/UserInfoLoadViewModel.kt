package com.srap.nga.ui.userinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.model.UserInfoResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = UserInfoLoadViewModel.ViewModelFactory::class)
class UserInfoLoadViewModel @AssistedInject constructor(
    @Assisted var id: Int,
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<TopicSubjectResponse.Result.Data>(networkRepo) {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(id: Int): UserInfoLoadViewModel
    }

    var result by mutableStateOf<UserInfoResponse.Result?>(null)
    var isUserInfoLoading by mutableStateOf(false)
        private set
    var isFollowLoading by mutableStateOf(false)

    init {
        getUserInfo()
    }

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getUserSubject(id, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            totalPage = state.response.totalPage
                            page = state.response.currentPage
                            list += state.response.result.data
                        }
                    }
                    super.fetchData()
                }
        }
    }

    override fun refresh() {
        getUserInfo()
        super.refresh()
    }

    private fun getUserInfo() {
        if (isUserInfoLoading) return
        isUserInfoLoading = true
        viewModelScope.launch {
            var errorMessage: String? = null
            try {
                repeat(USER_INFO_LOAD_ATTEMPTS) { attempt ->
                    var succeeded = false
                    networkRepo.getUserInfo(id)
                        .collect { state ->
                            when (state) {
                                is LoadingState.Error -> {
                                    errorMessage = state.errMsg
                                }
                                is LoadingState.Success -> {
                                    result = state.response.result
                                    succeeded = true
                                }
                            }
                        }

                    if (succeeded) return@launch
                    if (attempt < USER_INFO_LOAD_ATTEMPTS - 1) {
                        delay(USER_INFO_RETRY_DELAY_MS)
                    }
                }

                errorMessage?.let(ToastUtils::show)
            } finally {
                isUserInfoLoading = false
            }
        }
    }

    fun toggleFollow() {
        val current = result ?: return
        if (isFollowLoading) return

        val shouldFollow = current.follow == 0
        isFollowLoading = true
        viewModelScope.launch {
            val request = if (shouldFollow) {
                networkRepo.followUser(id)
            } else {
                networkRepo.unfollowUser(id)
            }
            request.collect { state ->
                when (state) {
                    is LoadingState.Error -> ToastUtils.show(state.errMsg)
                    is LoadingState.Success -> {
                        result = result?.copy(follow = if (shouldFollow) 1 else 0)
                    }
                }
                isFollowLoading = false
            }
        }
    }

    private companion object {
        const val USER_INFO_LOAD_ATTEMPTS = 2
        const val USER_INFO_RETRY_DELAY_MS = 500L
    }
}
