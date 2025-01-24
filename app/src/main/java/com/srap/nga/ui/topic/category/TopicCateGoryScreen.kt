package com.srap.nga.ui.topic.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.button.SearchButton
import com.srap.nga.ui.component.card.ImageTextCard
import com.srap.nga.ui.component.card.LoadingCard
import kotlinx.coroutines.launch

/**
 * 社区列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCateGoryScreen(
    onViewTopicSubject: (Int) -> Unit,
    onSearch: () -> Unit,
) {
    val viewModel: TopicCateGoryViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var pageState: PagerState

    val result = viewModel.result

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
                    Text("社区")
                },
                actions = {
                    SearchButton {
                        onSearch()
                    }
                }
            )
        }
    ) { innerPadding ->
        if (result == null) {
            LoadingCard(modifier = Modifier.padding(innerPadding))
        } else {
            pageState = rememberPagerState(
                initialPage = 0,
                pageCount = {
                    result.result.size
                }
            )

            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                // 右侧选项卡
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.22f)
                ) {
                    itemsIndexed(result.result) { index, item ->
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        pageState.scrollToPage(index)
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(3.dp)
                                    .background(
                                        if (index == pageState.currentPage) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surface
                                    )
                            )
                            Text(
                                text = item.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (index == pageState.currentPage)
                                            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .padding(8.dp)
                                    .align(Alignment.CenterVertically),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = if (index == pageState.currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 左侧社区选择栏
                VerticalPager(
                    state = pageState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.78f),
                    userScrollEnabled = false,
                ) { index ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        result.result[index].groups.forEach { group ->
                            item {
                                Text(group.name)
                            }

                            items(group.forums) { forum ->
                                ImageTextCard(
                                    image = NetworkModule.NGA_APP_ICON_URL.format(forum.id),
                                    title = forum.name,
                                    description = forum.info,
                                    modifier = Modifier
                                        .clickable {
                                            onViewTopicSubject(forum.fid)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}