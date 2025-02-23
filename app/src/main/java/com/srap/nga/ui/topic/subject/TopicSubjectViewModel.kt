package com.srap.nga.ui.topic.subject

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.TopicSubjectResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseViewModel
import com.srap.nga.utils.ToastUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.jvm.javaClass

private const val TAG = "TopicSubjectViewModel"

@HiltViewModel(assistedFactory = TopicSubjectViewModel.ViewModelFactory::class)
class TopicSubjectViewModel @AssistedInject constructor(
    @Assisted var id: Int,
    networkRepo: NetworkRepo,
) : BaseViewModel(networkRepo) {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(id: Int): TopicSubjectViewModel
    }

    var result by mutableStateOf<TopicSubjectResponse?>(null)

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            networkRepo.getTopicSubject(id)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            result = state.response
                        }
                    }
                }
        }
    }

    fun addCateGoryFavor() {
        viewModelScope.launch {
            networkRepo.addCateGoryFavor(id)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            if (state.response.result.isNotEmpty()) {
                                ToastUtil.show(state.response.result[0])
                            } else {
                                ToastUtil.show(state.response.msg)
                            }
                        }
                    }
                }
        }
    }

    fun delCateGoryFavor() {
        viewModelScope.launch {
            networkRepo.delCateGoryFavor(id)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            if (state.response.result.isNotEmpty()) {
                                ToastUtil.show(state.response.result[0])
                            } else {
                                ToastUtil.show(state.response.msg)
                            }
                        }
                    }
                }
        }
    }
}