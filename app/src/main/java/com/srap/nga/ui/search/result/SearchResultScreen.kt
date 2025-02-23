package com.srap.nga.ui.search.result

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.base.BaseRefreshLoadViewModel
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.card.ImageTextCard
import com.srap.nga.ui.component.card.ImageTextVerticalCard
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.tab.FancyTab
import com.srap.nga.ui.component.tab.SearchResultTag
import com.srap.nga.ui.component.topic.TopicSubjectCard
import com.srap.nga.utils.noRippleClickable
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    key: String,
    onViewPost: (Int) -> Unit,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    onBackClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val searchResultTags = listOf(
        SearchResultTag(
            title = "帖子",
            viewModel = hiltViewModel<SearchSubjectResultLoadViewModel, SearchSubjectResultLoadViewModel.ViewModelFactory>(key = "${key}SubjectLoad") { factory ->
                factory.create(key)
            }
        ),
        SearchResultTag(
            title = "社区",
            viewModel = hiltViewModel<SearchForumResultLoadViewModel, SearchForumResultLoadViewModel.ViewModelFactory>(key = "${key}ForumLoad") { factory ->
                factory.create(key)
            }
        ),
    )
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
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable { onBackClick() },
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(key)
                    }
                }
            )
        }
    ) { innerPadding ->
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { searchResultTags.size }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(pagerState.currentPage, matchContentSize = true)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    )
                },
            ) {
                searchResultTags.forEachIndexed { index, item ->
                    FancyTab(
                        title = item.title,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        selected = (index == pagerState.currentPage)
                    )
                }
            }

            HorizontalDivider()

            HorizontalPager(
                state = pagerState,
            ) { index ->
                val viewModel = searchResultTags[index].viewModel
                when (viewModel) {
                    is SearchSubjectResultLoadViewModel -> {
                        // 帖子
                        RefreshLoadList(
                            viewModel = viewModel,
                        ) { index, item ->
                            TopicSubjectCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clickable {
                                        onViewPost(item.tid)
                                    },
                                title = item.subject,
                                name = item.author,
                                count = item.replies,
                            )
                        }
                    }
                    is SearchForumResultLoadViewModel -> {
                        // 社区
                        RefreshLoadList(
                            viewModel = viewModel,
                        ) { index, item ->
                            ImageTextCard(
                                image= NetworkModule.NGA_APP_ICON_URL.format(item.id),
                                title = item.name,
                                description = item.parent.name,
                                modifier = Modifier
                                    .clickable {
                                        onViewTopicSubject(item.fid, null)
                                    }
                            )
                        }
                    }
                    else -> Text("异常")
                }
            }
        }
    }
}