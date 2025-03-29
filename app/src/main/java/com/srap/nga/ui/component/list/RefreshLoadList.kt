package com.srap.nga.ui.component.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.srap.nga.ui.base.BaseRefreshLoadViewModel

@Composable
fun <T> RefreshLoadList(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    RefreshLoadBase(
        viewModel = viewModel,
        modifier = modifier,
        state = listState
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            content = content
        )
    }
}
