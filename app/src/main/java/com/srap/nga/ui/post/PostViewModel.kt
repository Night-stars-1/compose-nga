package com.srap.nga.ui.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.PostResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

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

    var replyQuantity = 0
        private set

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getPost(id, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            response = state.response
                            response?.let {
                                list += it.result
                                page = it.currentPage
                                totalPage = it.totalPage
                                replyQuantity = it.vrows - 1
                            }
                        }
                    }
                    super.fetchData()
                }
        }
    }

}