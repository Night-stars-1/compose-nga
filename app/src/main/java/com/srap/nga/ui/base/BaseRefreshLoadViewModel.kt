package com.srap.nga.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.srap.nga.logic.repository.NetworkRepo

abstract class BaseRefreshLoadViewModel<T>(
    override val networkRepo: NetworkRepo,
    oldList: List<T> = emptyList(),
    oldTotalPage: Int = 1,
) : BaseViewModel(networkRepo) {

    var list by mutableStateOf(oldList)

    /**
     * 当前页数
     */
    var page = 1

    /**
     * 总页数
     */
    var totalPage = oldTotalPage

    var isEmpty by mutableStateOf(page == totalPage)
    var isRefreshing by mutableStateOf(false)
    var isLoadMore by mutableStateOf(false)

    open fun fetchData() {
        isLoaded = true
        isLoadMore = false
        isRefreshing = false
    }

    open fun refresh() {
        if (!isRefreshing && !isLoadMore) {
            list = emptyList()
            page = 1
            isLoadMore = false
            isRefreshing = true
            fetchData()
        }
    }

    open fun loadMore() {
        if (!isRefreshing && !isLoadMore && page < totalPage) {
            page++
            isLoadMore = true
            isRefreshing = false
            fetchData()
        }
    }
}