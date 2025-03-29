package com.srap.nga.ui.search.result

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.SubjectBySearchResponse
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


@HiltViewModel(assistedFactory = SearchSubjectResultLoadViewModel.ViewModelFactory::class)
class SearchSubjectResultLoadViewModel @AssistedInject constructor(
    @Assisted("key") var key: String,
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<SubjectBySearchResponse.Result.Data>(networkRepo) {
    private val TAG = javaClass.simpleName

    @AssistedFactory
    interface ViewModelFactory {
        fun create(
            @Assisted("key") key: String
        ): SearchSubjectResultLoadViewModel
    }

    var result by mutableStateOf<SubjectBySearchResponse?>(null)

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getSubjectBySearch(key, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            val response = state.response
                            if (response.result != null) {
                                result = response
                                page = response.currentPage
                                totalPage = response.totalPage
                                list += response.result.data
                            }
                        }
                    }
                    super.fetchData()
                }
        }
    }
}