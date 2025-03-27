package com.srap.nga.ui.component.modal

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.ui.component.fav.FavCheckBox
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavModal(
    state: FavState
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (state.showBottomSheet) {
        ModalBottomActionSheet(
            onDismissRequest = {
                state.showBottomSheet = false
            },
            sheetState = sheetState,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "选择收藏夹",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
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
                        state.childCheckedStates.forEachIndexed { index, check ->
                            if (check.value) {
                                val data = state.viewModel.list[index]
                                state.viewModel.addFavorite(state.postId, data.id)
                            }
                        }
                    }
                ) {
                    Text("确认")
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(state.viewModel.list) { index, item ->
                    FavCheckBox(
                        title = item.name,
                        description = "${item.length}条内容·${item.type}",
                        checked = state.childCheckedStates[index]
                    )
                }
            }
        }
    }
}

@Composable
fun rememberFavState(): FavState {
    val viewModel: FavViewModel = hiltViewModel()
    return FavState(viewModel)
}

@Stable
open class FavState(
    val viewModel: FavViewModel
) {
    var showBottomSheet by mutableStateOf(false)
    var postId: Int = 0

    var childCheckedStates: MutableList<MutableState<Boolean>> = mutableListOf()

    /**
     * 打开收藏弹窗
     * @param id 帖子ID
     */
    fun open(id: Int) {
        showBottomSheet = true
        postId = id

        // 请求前刷新状态，避免用户在UI上看见状态变更
        updateCheckedStates()
        viewModel.fetchData()

        viewModel.onDataFetched = {
            updateCheckedStates()
        }
    }


    /**
     * 更新选择状态
     */
    fun updateCheckedStates() {
        if (childCheckedStates.isEmpty()) {
            childCheckedStates = viewModel.list.map { mutableStateOf(it.default) }.toMutableList()
        } else {
            repeat(viewModel.list.size) {
                childCheckedStates[it].value = viewModel.list[it].default
            }
        }
    }
}