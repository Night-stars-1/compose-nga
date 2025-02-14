package com.srap.nga.ui.topic.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.card.LoadingCard
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.tab.FancyTab
import com.srap.nga.ui.component.tab.SearchResultTag
import com.srap.nga.ui.component.topic.TopicSubjectCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicSubjectScreen(
    id: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<TopicSubjectViewModel, TopicSubjectViewModel.ViewModelFactory>(key = id.toString()) { factory ->
        factory.create(id)
    }
    val result = viewModel.result

    val scope = rememberCoroutineScope()
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
                    Text(viewModel.result?.forumName ?: "主题")
                }
            )
        }
    ) { innerPadding ->
        if (result == null) {
            LoadingCard()
        } else {
            val data = result.result
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = {
                    data.subForum.size + 1
                }
            )

            val loadViewModelList = listOf(
                SearchResultTag(
                    title = "全部",
                    viewModel = hiltViewModel<TopicSubjectLoadViewModel, TopicSubjectLoadViewModel.ViewModelFactory>(key = "${id}load") { factory ->
                        factory.create(id, data.data, result.totalPage)
                    }
                )
            ) + data.subForum.mapIndexed { index, item ->
                SearchResultTag(
                    title = item.name,
                    viewModel = hiltViewModel<TopicSubjectLoadViewModel, TopicSubjectLoadViewModel.ViewModelFactory>(key = "${id}load${index}") { factory ->
                        factory.create(item.id)
                    }
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                SecondaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier
                                .tabIndicatorOffset(pagerState.currentPage, matchContentSize = true)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        )
                    },
                ) {
                    loadViewModelList.forEachIndexed { index, item ->
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
                    val viewModel = loadViewModelList[index].viewModel
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
                            images = item.attachs?.map { Pair(NetworkModule.NGA_ATTACHMENTS_URL.format(it.attachUrl), "${item.authorId}${it.attachUrl}") },
                            name = item.author,
                            count = item.replies,
                        )
                    }
                }
            }
        }
    }
}

