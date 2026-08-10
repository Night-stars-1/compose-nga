package com.srap.nga.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.card.SearchItemCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onViewSearchResult: (String) -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val history by viewModel.history.collectAsState()

    val focusRequest = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        try {
            // 打开界面，主动拉起输入法
            focusRequest.requestFocus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var textInput by rememberSaveable { mutableStateOf("") }
    val submitSearch: (String) -> Unit = { query ->
        viewModel.recordSearch(query)?.let(onViewSearchResult)
    }
    LaunchedEffect(textInput) {
        delay(300)
        viewModel.fetchData(textInput.trim())
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(
                        WindowInsets.systemBars.only(
                            WindowInsetsSides.Start +
                                WindowInsetsSides.Top +
                                WindowInsetsSides.End
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                DockedSearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = textInput,
                            onQueryChange = { textInput = it },
                            onSearch = submitSearch,
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text("搜索社区或帖子") },
                            leadingIcon = {
                                BackButton { onBackClick() }
                            },
                            trailingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AnimatedVisibility(
                                        visible = textInput.isNotEmpty(),
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ) {
                                        IconButton(onClick = { textInput = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "清空搜索内容",
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { submitSearch(textInput) },
                                        enabled = textInput.isNotBlank(),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "搜索",
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequest),
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {}
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (textInput.isBlank()) {
                if (history.isNotEmpty()) {
                    item(key = "search-history-header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 8.dp, top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "搜索历史",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            IconButton(onClick = viewModel::clearHistory) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = "清空搜索历史",
                                )
                            }
                        }
                    }
                    items(
                        items = history,
                        key = { "search-history:$it" },
                    ) { query ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = query,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.History,
                                    contentDescription = null,
                                )
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { viewModel.removeHistory(query) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "删除搜索记录：$query",
                                    )
                                }
                            },
                            modifier = Modifier.clickable { submitSearch(query) },
                        )
                    }
                }
            } else {
                items(viewModel.result) {
                    SearchItemCard(
                        title = it,
                        startIcon = Icons.Default.Search,
                        endIcon = Icons.Default.NorthWest,
                        modifier = Modifier
                            .clickable {
                                submitSearch(it)
                            }
                    )
                }
            }
        }
    }
}
