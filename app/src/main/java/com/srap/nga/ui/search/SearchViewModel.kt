package com.srap.nga.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.preferences.SearchHistoryStore
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseViewModel
import com.srap.nga.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    networkRepo: NetworkRepo,
    private val searchHistoryStore: SearchHistoryStore,
) : BaseViewModel(networkRepo) {

    var result by mutableStateOf<List<String>>(emptyList())
    private var searchJob: Job? = null
    val history = searchHistoryStore.history

    fun recordSearch(query: String): String? = searchHistoryStore.record(query)

    fun removeHistory(query: String) {
        searchHistoryStore.remove(query)
    }

    fun clearHistory() {
        searchHistoryStore.clear()
    }

    fun fetchData(word: String) {
        searchJob?.cancel()
        if (word.isBlank()) {
            result = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            networkRepo.getSearchPrompt(word)
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtils.show(state.errMsg)
                        }
                        is LoadingState.Success -> {
                            if (state.response.result.isNotEmpty()) {
                                result = state.response.result[0].recomList
                            } else {
                                result = emptyList()
                            }
                        }
                    }
                }
        }
    }
}
