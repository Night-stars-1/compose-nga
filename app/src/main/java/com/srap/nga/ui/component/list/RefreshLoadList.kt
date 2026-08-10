package com.srap.nga.ui.component.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.srap.nga.ui.base.BaseRefreshLoadViewModel

@Composable
fun <T> RefreshLoadList(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    showRefreshIndicator: Boolean = true,
    initialLoadAsRefresh: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit,
) {
    RefreshLoadBase(
        viewModel = viewModel,
        modifier = modifier,
        state = listState,
        showRefreshIndicator = showRefreshIndicator,
        initialLoadAsRefresh = initialLoadAsRefresh,
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}
