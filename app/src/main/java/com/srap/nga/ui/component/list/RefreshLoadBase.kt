package com.srap.nga.ui.component.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.srap.nga.ui.base.BaseRefreshLoadViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> RefreshLoadContent(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    showRefreshIndicator: Boolean = true,
    initialLoadAsRefresh: Boolean = true,
    content: @Composable () -> Unit,
) {
    val state = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        if (!viewModel.isLoaded && viewModel.list.isEmpty()) {
            if (initialLoadAsRefresh) {
                viewModel.refresh()
            } else {
                viewModel.fetchData()
            }
        }
    }

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = viewModel.isRefreshing,
        state = state,
        onRefresh = viewModel::refresh,
        indicator = {
            if (showRefreshIndicator) {
                PullToRefreshDefaults.LoadingIndicator(
                    state = state,
                    isRefreshing = viewModel.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content()

            AnimatedVisibility(
                visible = viewModel.isLoadMore,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                LoadingIndicator(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(32.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RefreshLoadBase(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    showRefreshIndicator: Boolean = true,
    initialLoadAsRefresh: Boolean = true,
    content: @Composable () -> Unit,
) {
    RefreshLoadContent(
        viewModel = viewModel,
        modifier = modifier,
        showRefreshIndicator = showRefreshIndicator,
        initialLoadAsRefresh = initialLoadAsRefresh,
        content = content,
    )

    // 检查是否滚动到底部
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                val totalItemsCount = layoutInfo.totalItemsCount
                // 判断是否滚动到底部并触发加载更多
                if (
                    lastVisibleItemIndex >= totalItemsCount - 1
                    && !viewModel.isLoadMore
                    && !viewModel.isRefreshing
                    && !viewModel.isEmpty
                ) {
                    viewModel.loadMore()
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RefreshLoadBase(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    showRefreshIndicator: Boolean = true,
    initialLoadAsRefresh: Boolean = true,
    content: @Composable () -> Unit,
) {
    RefreshLoadContent(
        viewModel = viewModel,
        modifier = modifier,
        showRefreshIndicator = showRefreshIndicator,
        initialLoadAsRefresh = initialLoadAsRefresh,
        content = content,
    )

    // 检查是否滚动到底部
    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                val totalItemsCount = layoutInfo.totalItemsCount
                // 判断是否滚动到底部并触发加载更多
                if (
                    lastVisibleItemIndex >= totalItemsCount - 4
                    && !viewModel.isLoadMore
                    && !viewModel.isRefreshing
                    && !viewModel.isEmpty
                ) {
                    viewModel.loadMore()
                }
            }
    }
}
