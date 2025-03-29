package com.srap.nga.ui.fav

import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.model.FavoriteContentResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.utils.ToastUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FavContentViewModel.ViewModelFactory::class)
class FavContentViewModel @AssistedInject constructor(
    @Assisted var id: Int,
    networkRepo: NetworkRepo,
) : BaseRefreshLoadViewModel<FavoriteContentResponse.Result.Data>(networkRepo) {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(id: Int): FavContentViewModel
    }

    override fun fetchData() {
        viewModelScope.launch {
            networkRepo.getFavoriteContent(id, page)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            list += state.response.result.data
                        }
                    }
                    super.fetchData()
                }
        }
    }
}
