package com.srap.nga.ui.component.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
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
    header: @Composable (() -> Unit) = {},
    itemWidget: @Composable (index: Int, item: T) -> Unit,
) {
    RefreshLoadBase(
        viewModel = viewModel,
        modifier = modifier,
        listState = listState
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            item {
                header()
            }

            itemsIndexed(viewModel.list) { index, item ->
                itemWidget(index, item)
            }
        }
    }
}
