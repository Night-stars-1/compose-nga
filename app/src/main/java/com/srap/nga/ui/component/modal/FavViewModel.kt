package com.srap.nga.ui.component.modal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.FavoriteResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseViewModel
import com.srap.nga.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavViewModel @Inject constructor(
    networkRepo: NetworkRepo,
) : BaseViewModel(networkRepo) {
    var list by mutableStateOf(emptyList<FavoriteResponse.Data>())

    var onDataFetched: (() -> Unit)? = null

     fun fetchData() {
         viewModelScope.launch {
             networkRepo.getFavorite()
                 .collect { state ->
                     when (state) {
                         is LoadingState.Error -> {
                             ToastUtils.show(state.errMsg)
                         }

                         is LoadingState.Success -> {
                             list = state.response.result
                         }
                     }
                     onDataFetched?.invoke()
                 }
         }
    }

    /**
     * 收藏
     * @param tid 帖子ID
     * @param folder 收藏夹ID
     */
    fun addFavorite(
        tid: Int,
        folder: Int,
    ) {
        viewModelScope.launch {
            networkRepo.addFavorite(tid, folder)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }

                        is LoadingState.Success -> {
                            ToastUtils.show(state.response.result[0])
                        }
                    }
                }
        }
    }
}
