package com.srap.nga.ui.userinfo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.model.UserInfoResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

private val TAG = "UserInfoLoadViewModel"

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

    init {
        getUserInfo()
    }

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getUserSubject(id, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
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

    private fun getUserInfo() {
        viewModelScope.launch {
            networkRepo.getUserInfo(id)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            result = state.response.result
                        }
                    }
                }
        }
    }
}