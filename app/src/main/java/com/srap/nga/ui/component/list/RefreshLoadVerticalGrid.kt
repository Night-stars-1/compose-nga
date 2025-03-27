package com.srap.nga.ui.component.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srap.nga.ui.base.BaseRefreshLoadViewModel

@Composable
fun <T> RefreshLoadVerticalGrid(
    viewModel: BaseRefreshLoadViewModel<T>,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    columns: GridCells,
    content: LazyGridScope.() -> Unit,
) {
    RefreshLoadBase(
        viewModel = viewModel,
        modifier = modifier,
        state = state,
        columns = columns,
    ) {
        LazyVerticalGrid(
            state = state,
            columns = columns,
            modifier = Modifier
                .fillMaxWidth(),
            content = content
        )
    }
}