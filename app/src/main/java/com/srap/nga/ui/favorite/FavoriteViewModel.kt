package com.srap.nga.ui.favorite

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.FavoriteResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtil
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<FavoriteResponse.Data>(networkRepo) {

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getFavorite(page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            list += state.response.result
                        }
                    }
                    super.fetchData()
                }
        }
    }
}
