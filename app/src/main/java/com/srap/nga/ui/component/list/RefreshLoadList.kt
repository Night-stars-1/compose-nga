package com.srap.nga.ui.component.list

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.srap.nga.ui.base.BaseRefreshLoadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RefreshLoadList(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    content: @Composable (BoxScope.() -> Unit) = {},
    itemWidget: @Composable (index: Int, item: T) -> Unit,
) {
    // 刷新功能
    val state = rememberPullToRefreshState()

    // 进入该界面后加载数据
    LaunchedEffect(Unit) {
        // 如果位加载并且默认数据为空则加载数据
        if (!viewModel.isLoaded && viewModel.list.isEmpty()) {
            viewModel.refresh()
        }
    }

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = viewModel.isRefreshing,
        state = state,
        onRefresh = viewModel::refresh
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            item {
                content()
            }

            itemsIndexed(viewModel.list) { index, item ->
                itemWidget(index, item)
            }

            // 加载更多的指示器
            item {
                if (!viewModel.isRefreshing) {
                    if (viewModel.isEmpty) {
                        Text("已经到底了...")
                    } else {
                        CircularProgressIndicator(modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }
    }

    // 检查是否滚动到底部
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
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
