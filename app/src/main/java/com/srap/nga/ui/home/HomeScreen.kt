package com.srap.nga.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.Alignment
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
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets.systemBars
                    .only(
                        WindowInsetsSides.Top + WindowInsetsSides.Start
                    ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
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
        RefreshLoadVerticalGrid(
            viewModel = viewModel,
            columns = GridCells.Adaptive(minSize = 168.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            items(viewModel.list) { item ->
                HomeCard(item=item, onViewPost=onViewPost, openUrl=openUrl)
            }
        }
    }
}

@Composable
fun HomeCard(
    item: RecTopicResponse.Result,
    onViewPost: (Int) -> Unit,
    openUrl: (String) -> Unit,
) {
    Card(
        onClick = {
            if (item.url != null) {
                openUrl(item.url)
            } else {
                onViewPost(item.tid)
            }
        },
        modifier = Modifier.padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 主内容
            Column {
                AsyncImage(
                    model = item.threadIcon.toNgaImageUrl(),
                    contentDescription = item.subject,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = item.subject,
                    modifier = Modifier.padding(12.dp),
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.topic != null) {
                // 左上角 Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.9f),
                            shape = MaterialTheme.shapes.extraSmall,
                        )
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = item.topic.parent[1].toString(),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
