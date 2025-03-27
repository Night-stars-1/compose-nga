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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import androidx.compose.ui.Alignment
import com.srap.nga.logic.model.RecTopicResponse
import com.srap.nga.ui.component.button.SearchButton
import com.srap.nga.ui.component.card.LoadingCard
import com.srap.nga.ui.component.list.RefreshLoadVerticalGrid

/**
 * 首页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onViewPost: (Int) -> Unit,
    onSearch: () -> Unit,
) {
    val viewModel: HomeLoadViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.systemBars
                    .only(
                        WindowInsetsSides.Top + WindowInsetsSides.Start
                    ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
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
            columns = GridCells.Fixed(2), // 每行固定 2 列
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            items(viewModel.list) { item ->
                HomeCard(item=item, onViewPost=onViewPost)
            }
        }
    }
}

@Composable
fun HomeCard(
    item: RecTopicResponse.Result,
    onViewPost: (Int) -> Unit,
) {
    Card(
        onClick = {
            onViewPost(item.tid)
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 主内容
            Column {
                AsyncImage(
                    model = item.threadIcon,
                    contentDescription = item.subject,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                )
                Text(
                    text = item.subject,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 左上角 Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f), // 半透明背景
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = item.topic.parent[1].toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}