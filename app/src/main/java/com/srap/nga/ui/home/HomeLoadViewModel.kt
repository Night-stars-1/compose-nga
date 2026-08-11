package com.srap.nga.ui.home

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.srap.nga.logic.model.RecTopicResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeLoadViewModel @Inject constructor(
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<RecTopicResponse.Result>(networkRepo) {
    val gson = Gson()

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getRecTopic(page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            val result = state.response.result
                            val data = result.getOrNull(0)?.let {
                                state.parseItem<RecTopicResponse.Result>(it)
                            }
                            val pageInfo = result.getOrNull(2)?.let {
                                runCatching {
                                    gson.fromJson(
                                        gson.toJson(it),
                                        RecTopicResponse.PageInfo::class.java,
                                    )
                                }.getOrNull()
                            }
                            if (data != null) {
                                list += data
                            }
                            if (pageInfo != null) {
                                page = pageInfo.currentPage
                                totalPage = pageInfo.perPage
                            }
                        }
                    }
                    super.fetchData()
                }
        }
    }
}
