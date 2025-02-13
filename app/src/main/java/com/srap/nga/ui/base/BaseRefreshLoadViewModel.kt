package com.srap.nga.ui.base

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    var page by mutableIntStateOf(1)

    /**
     * 总页数
     */
    var totalPage by mutableIntStateOf(oldTotalPage)

    /** 是否当前页面与总页面相等 */
    val isEmpty by derivedStateOf { page == totalPage }
    /** 是否正在刷新 */
    var isRefreshing by mutableStateOf(false)
    /** 是否正在加载更多内容 */
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