package com.srap.nga.ui.topic.subject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtils
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
    @Assisted("attachPrefix") oldAttachPrefix: String,
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<TopicSubjectResponse.Result.Data>(networkRepo, oldList, oldTotalPage) {

    init {
        // The first tab is seeded from TopicSubjectViewModel's response.
        if (oldList.isNotEmpty()) {
            isLoaded = true
        }
    }

    @AssistedFactory
    interface ViewModelFactory {
        fun create(
            @Assisted("id") id: Int,
            @Assisted("list")list: List<TopicSubjectResponse.Result.Data> = emptyList(),
            @Assisted("totalPage") totalPage: Int = 1,
            @Assisted("attachPrefix") attachPrefix: String = ""
        ): TopicSubjectLoadViewModel
    }

    var result by mutableStateOf<TopicSubjectResponse?>(null)
    var attachPrefix by mutableStateOf(oldAttachPrefix)

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getTopicSubject(id, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            val response = state.response
                            result = response
                            if (response.result.attachPrefix.isNotBlank()) {
                                attachPrefix = response.result.attachPrefix
                            }
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
