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
                            val result = state.response.result as List<*>
                            val data = state.parseItem<RecTopicResponse.Result>(result[0])
                            val json = gson.toJson(result[2])
                            val pageInfo = gson.fromJson(json, RecTopicResponse.PageInfo::class.java)
                            if (data != null) {
                                list += data

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