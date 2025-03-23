package com.srap.nga.ui.topic.subject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TopicSubjectLoadViewModel.ViewModelFactory::class)
class TopicSubjectLoadViewModel @AssistedInject constructor(
    @Assisted("id") var id: Int,
    @Assisted("list") var oldList: List<TopicSubjectResponse.Result.Data>,
    @Assisted("totalPage") var oldTotalPage: Int,
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<TopicSubjectResponse.Result.Data>(networkRepo, oldList, oldTotalPage) {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(
            @Assisted("id") id: Int,
            @Assisted("list")list: List<TopicSubjectResponse.Result.Data> = emptyList(),
            @Assisted("totalPage") totalPage: Int = 1
        ): TopicSubjectLoadViewModel
    }

    var result by mutableStateOf<TopicSubjectResponse?>(null)

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getTopicSubject(id, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            val response = state.response
                            result = response
                            page = response.currentPage
                            totalPage = response.totalPage
                            list += response.result.data
                        }
                    }
                    super.fetchData()
                }
        }
    }
}