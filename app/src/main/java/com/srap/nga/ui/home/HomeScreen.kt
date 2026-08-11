package com.srap.nga.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.srap.nga.logic.model.RecTopicResponse
import com.srap.nga.ui.component.button.SearchButton
import com.srap.nga.ui.component.list.RefreshLoadVerticalGrid
import com.srap.nga.utils.toNgaImageUrl

/**
 * 首页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onViewPost: (Int) -> Unit,
    onSearch: () -> Unit,
    openUrl: (String) -> Unit,
) {
    val viewModel: HomeLoadViewModel = hiltViewModel()
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
                title = {
                    Text("推荐")
                },
                actions = {
                    SearchButton {
                        onSearch()
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            val columns = if (maxWidth < 600.dp) {
                GridCells.Fixed(2)
            } else {
                GridCells.Adaptive(minSize = 220.dp)
            }
            RefreshLoadVerticalGrid(
                viewModel = viewModel,
                columns = columns,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 64.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = viewModel.list,
                    key = { item -> item.tid.takeIf { it != 0 } ?: item.id },
                ) { item ->
                    HomeCard(
                        item = item,
                        onViewPost = onViewPost,
                        openUrl = openUrl,
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    item: RecTopicResponse.Result,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topicLabel = item.topic?.parent
        ?.getOrNull(1)
        ?.toString()
        ?.trim()
        ?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }

    Card(
        onClick = {
            val url = item.url?.trim()?.takeIf { it.isNotEmpty() }
            if (url != null) {
                openUrl(url)
            } else {
                onViewPost(item.tid)
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.2f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Topic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(32.dp),
                )
                AsyncImage(
                    model = item.threadIcon?.toNgaImageUrl().orEmpty(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                if (topicLabel != null) {
                    Text(
                        text = topicLabel,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 10.dp,
                ),
            ) {
                Text(
                    text = item.subject,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
