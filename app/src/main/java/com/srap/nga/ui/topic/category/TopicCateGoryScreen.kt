package com.srap.nga.ui.topic.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.logic.model.CateGoryFavorResponse
import com.srap.nga.logic.model.TopicCateGoryResponse
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
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    onSearch: () -> Unit,
) {
    val viewModel: TopicCateGoryViewModel = hiltViewModel()
    val favorViewModel: CateGoryFavorViewModel = hiltViewModel()
    val result = viewModel.result
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
            val pageState = rememberPagerState(
                initialPage = 0,
                pageCount = {
                    result.result.size + 1
                }
            )

            BoxWithConstraints(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                val titles = listOf("关注") + result.result.map { it.name }
                if (maxWidth < 600.dp) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SecondaryScrollableTabRow(
                            selectedTabIndex = pageState.currentPage,
                            edgePadding = 8.dp,
                        ) {
                            titles.forEachIndexed { index, title ->
                                CategoryTab(pageState, index, title)
                            }
                        }
                        CategoryPager(
                            pageState = pageState,
                            result = result,
                            favoriteResult = favorViewModel.result,
                            onViewTopicSubject = onViewTopicSubject,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(min = 176.dp, max = 240.dp)
                                .padding(vertical = 8.dp),
                        ) {
                            itemsIndexed(titles) { index, title ->
                                SelectCard(pageState, index, title)
                            }
                        }
                        CategoryPager(
                            pageState = pageState,
                            result = result,
                            favoriteResult = favorViewModel.result,
                            onViewTopicSubject = onViewTopicSubject,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

/**
 * 左侧选项栏
 */
@Composable
fun SelectCard(
    pageState: PagerState,
    index: Int,
    title: String
) {
    val scope = rememberCoroutineScope()
    NavigationDrawerItem(
        label = { Text(title) },
        selected = index == pageState.currentPage,
        onClick = {
            scope.launch {
                pageState.animateScrollToPage(index)
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        shape = MaterialTheme.shapes.medium,
    )
}

@Composable
private fun CategoryTab(
    pageState: PagerState,
    index: Int,
    title: String,
) {
    val scope = rememberCoroutineScope()
    Tab(
        selected = index == pageState.currentPage,
        onClick = {
            scope.launch {
                pageState.animateScrollToPage(index)
            }
        },
        text = { Text(title) },
    )
}

@Composable
private fun CategoryPager(
    pageState: PagerState,
    result: TopicCateGoryResponse,
    favoriteResult: List<CateGoryFavorResponse.Result>,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pageState,
        modifier = modifier.fillMaxHeight(),
    ) { index ->
        if (index == 0) {
            FavorContentCard(
                result = favoriteResult,
                onViewTopicSubject = onViewTopicSubject,
                iconPrefix = result.forumIconPre,
            )
        } else {
            ContentCard(
                result = result.result[index - 1].groups,
                onViewTopicSubject = onViewTopicSubject,
                iconPrefix = result.forumIconPre,
            )
        }
    }
}

/**
 * 右侧社区选择栏
 */
@Composable
fun ContentCard(
    result: List<TopicCateGoryResponse.Result.Group>,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    iconPrefix: String? = null,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        result.forEach { group ->
            item {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }

            items(group.forums) { forum ->
                ImageTextCard(
                    image = forumIconUrl(iconPrefix, forum.id),
                    title = forum.name,
                    description = forum.info,
                    modifier = Modifier
                        .clickable {
                            onViewTopicSubject(forum.fid, false)
                        }
                )
            }
        }
    }
}

/**
 * 右侧关注社区选择栏
 */
@Composable
fun FavorContentCard(
    result: List<CateGoryFavorResponse.Result>,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    iconPrefix: String? = null,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Text(
                text = "关注",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        items(result) { forum ->
            ImageTextCard(
                image = forumIconUrl(iconPrefix, forum.id),
                title = forum.name,
                description = "",
                modifier = Modifier
                    .clickable {
                        onViewTopicSubject(forum.fid, true)
                    }
            )
        }
    }
}

private fun forumIconUrl(prefix: String?, id: Int): String {
    val rawPrefix = prefix?.trim().orEmpty()
    val normalizedPrefix = when {
        rawPrefix.startsWith("//") -> "https:" + rawPrefix
        rawPrefix.startsWith("http://", ignoreCase = true) ->
            "https://" + rawPrefix.substringAfter("://")
        rawPrefix.startsWith("https://", ignoreCase = true) -> rawPrefix
        else -> NetworkModule.NGA_APP_ICON_URL.substringBefore("%s")
    }.trimEnd('/')

    return normalizedPrefix + "/" + id + ".png"
}
