package com.srap.nga.ui.search.result

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.ForumBySearchResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.collections.plus


@HiltViewModel(assistedFactory = SearchForumResultLoadViewModel.ViewModelFactory::class)
class SearchForumResultLoadViewModel @AssistedInject constructor(
    @Assisted("key") var key: String,
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<ForumBySearchResponse.Result>(networkRepo) {
    private val TAG = javaClass.simpleName

    @AssistedFactory
    interface ViewModelFactory {
        fun create(
            @Assisted("key") key: String
        ): SearchForumResultLoadViewModel
    }

    var result by mutableStateOf<ForumBySearchResponse?>(null)

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getForumBySearch(key, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            val response = state.response
                            if (response.result != null) {
                                result = response
                                page = state.response.currentPage
                                totalPage = state.response.totalPage ?: 1
                                list += response.result
                            }
                        }
                    }
                    super.fetchData()
                }
        }
    }
}