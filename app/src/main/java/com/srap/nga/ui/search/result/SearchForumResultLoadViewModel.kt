package com.srap.nga.ui.search.result

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.ForumBySearchResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtil
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
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            result = state.response
                            if (result != null) {
                                page = result!!.currentPage
                                totalPage = result!!.totalPage ?: 1
                                list += result!!.result
                            }
                        }
                    }
                    super.fetchData()
                }
        }
    }
}