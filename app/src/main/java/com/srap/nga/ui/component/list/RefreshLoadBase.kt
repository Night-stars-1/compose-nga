package com.srap.nga.ui.component.list

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.ui.component.state.rememberSwipeableState
import com.srap.nga.utils.swipeable
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RefreshLoadContent(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // 刷新功能
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    val loadDistance = with(LocalDensity.current) { 70.dp.toPx() }
    val swipeableState = rememberSwipeableState(
        anchors = Triple(-loadDistance, -30f, loadDistance),
        heightRange = Pair(-300f, 300f)
    )

    // 进入该界面后加载数据
    LaunchedEffect(Unit) {
        // 如果位加载并且默认数据为空则加载数据
        if (!viewModel.isLoaded && viewModel.list.isEmpty()) {
            viewModel.refresh()
        }
    }
    viewModel.loadedCallback = {
        coroutineScope.launch {
            swipeableState.close()
        }
    }

    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = viewModel.isRefreshing,
        state = state,
        onRefresh = viewModel::refresh,
    ) {
        Box(
            modifier = Modifier
                .swipeable(
                    state = swipeableState,
                    orientation = Orientation.Vertical,
                )
                .fillMaxSize()
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            content()

            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) },
            ) {
                ElevatedCard(
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    if (viewModel.isEmpty) {
                        Text(
                            "已经到底了...",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.padding(6.dp))
                    }
                }
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
    content: @Composable () -> Unit,
) {
    RefreshLoadContent(
        viewModel = viewModel,
        modifier = modifier,
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
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    content: @Composable () -> Unit,
) {
    RefreshLoadContent(
        viewModel = viewModel,
        modifier = modifier,
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
                    lastVisibleItemIndex >= totalItemsCount + columns.hashCode()
                    && !viewModel.isLoadMore
                    && !viewModel.isRefreshing
                    && !viewModel.isEmpty
                ) {
                    viewModel.loadMore()
                }
            }
    }
}
