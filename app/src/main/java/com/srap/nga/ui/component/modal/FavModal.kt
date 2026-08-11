package com.srap.nga.ui.component.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.logic.model.FavoriteResponse
import com.srap.nga.ui.component.fav.FavCheckBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavModal(
    state: FavState
) {
    val viewModel: FavViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var readyRequestId by remember { mutableIntStateOf(-1) }
    val openRequestId = state.openRequestId

    LaunchedEffect(state.showBottomSheet, openRequestId) {
        if (state.showBottomSheet) {
            state.checkedFolders.clear()
            val loaded = viewModel.fetchData()
            if (state.showBottomSheet && state.openRequestId == openRequestId) {
                if (loaded) {
                    state.syncCheckedFolders(viewModel.list)
                    readyRequestId = openRequestId
                } else {
                    state.showBottomSheet = false
                }
            }
        }
    }

    if (state.showBottomSheet && readyRequestId == openRequestId) {
        ModalBottomActionSheet(
            onDismissRequest = {
                state.showBottomSheet = false
            },
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            fillContent = true,
            keepActionsVisible = true,
            actions = {
                FilledTonalButton(
                    onClick = {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    state.showBottomSheet = false
                                }
                            }
                    }
                ) {
                    Text("取消")
                }
                Button(
                    onClick = {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    state.showBottomSheet = false
                                }
                            }
                        viewModel.list.forEach { data ->
                            if (state.checkedFolders[data.id] == true) {
                                viewModel.addFavorite(state.postId, data.id)
                            }
                        }
                    }
                ) {
                    Text("确认")
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "选择收藏夹",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
                ) {
                    itemsIndexed(
                        items = viewModel.list,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        FavCheckBox(
                            title = item.name,
                            description = "${item.length} 条内容 · ${item.type}",
                            checked = state.checkedFolders[item.id] ?: item.default,
                            onCheckedChange = { checked ->
                                state.checkedFolders[item.id] = checked
                            },
                            index = index,
                            count = viewModel.list.size,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberFavState(): FavState {
    return remember { FavState() }
}

@Stable
open class FavState {
    var showBottomSheet by mutableStateOf(false)
    var postId: Int = 0
        private set
    var openRequestId by mutableIntStateOf(0)
        private set

    val checkedFolders = mutableStateMapOf<Int, Boolean>()

    /**
     * 打开收藏弹窗
     * @param id 帖子ID
     */
    fun open(id: Int) {
        if (showBottomSheet) return

        postId = id
        openRequestId += 1
        showBottomSheet = true
    }


    /**
     * 更新选择状态
     */
    fun syncCheckedFolders(folders: List<FavoriteResponse.Data>) {
        checkedFolders.clear()
        folders.forEach { folder ->
            checkedFolders[folder.id] = folder.default
        }
    }
}
