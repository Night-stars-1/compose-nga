package com.srap.nga.ui.fav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.ui.component.IconText
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList

/**
 * 收藏夹内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteContentScreen(
    id: Int,
    title: String,
    onViewPost: (id: Int) -> Unit,
    onBackClick: (() -> Unit)?,
) {
    val viewModel: FavContentViewModel =
        hiltViewModel<FavContentViewModel, FavContentViewModel.ViewModelFactory>(
            key = "${id}favorite",
        ) { factory ->
            factory.create(id)
        }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.systemBars
                    .only(
                        WindowInsetsSides.Top + WindowInsetsSides.Start
                    ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                navigationIcon = {
                    if (onBackClick != null) BackButton { onBackClick() }
                },
                title = {
                    Text(title)
                }
            )
        }
    ) { innerPadding ->
        RefreshLoadList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 64.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            itemsIndexed(
                items = viewModel.list,
                key = { _, item -> item.tid },
            ) { index, item ->
                SegmentedListItem(
                    onClick = {
                        onViewPost(item.tid)
                    },
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = viewModel.list.size,
                    ),
                    supportingContent = {
                        Text(
                            text = "${item.author} · ${item.forumname}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    trailingContent = {
                        IconText(
                            text = item.replies.toString(),
                            icon = Icons.AutoMirrored.Filled.Chat,
                        )
                    },
                ) {
                    Text(
                        text = item.subject,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
