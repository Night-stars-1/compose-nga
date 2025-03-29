package com.srap.nga.ui.topic.category

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.CateGoryFavorResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseViewModel
import com.srap.nga.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CateGoryFavorViewModel @Inject constructor(
    networkRepo: NetworkRepo,
) : BaseViewModel(networkRepo) {

    var result by mutableStateOf<List<CateGoryFavorResponse.Result>>(listOf())

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            networkRepo.getCateGoryFavor()
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            if (state.response.result.isNotEmpty()) {
                                result = state.response.result[0]
                            }
                        }
                    }
                }
        }
    }
}