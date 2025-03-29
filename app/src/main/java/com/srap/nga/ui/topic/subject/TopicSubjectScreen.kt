package com.srap.nga.ui.topic.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.card.LoadingCard
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.tab.FancyTab
import com.srap.nga.ui.component.tab.SearchResultTag
import com.srap.nga.ui.component.topic.TopicSubjectCard
import com.srap.nga.utils.toHttps
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicSubjectScreen(
    id: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
    isFavor: Boolean?,
) {
    var isFavorState by remember { mutableStateOf(isFavor) }
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
                },
                actions = {
                    if (isFavorState == true) {
                        IconButton(
                            onClick = {
                                viewModel.delCateGoryFavor()
                                isFavorState = false
                            },
                        ) {
                            Icon(Icons.Filled.Grade, contentDescription="收藏")
                        }
                    } else if (isFavorState == false) {
                        IconButton(
                            onClick = {
                                viewModel.addCateGoryFavor()
                                isFavorState = true
                            },
                        ) {
                            Icon(Icons.Outlined.Grade, contentDescription="收藏")
                        }
                    }
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
                    .fillMaxHeight()
                    .padding(innerPadding)
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
                    modifier = Modifier.fillMaxHeight()
                ) { index ->
                    val viewModel = loadViewModelList[index].viewModel
                    RefreshLoadList(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        items(viewModel.list) { item ->
                            TopicSubjectCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 8.dp)
                                    .clickable {
                                        onViewPost(item.tid)
                                    },
                                title = item.subject,
                                images = item.attachs?.map {
                                    Pair(
                                        NetworkModule.NGA_ATTACHMENTS_URL.format(
                                            it.attachUrl
                                        ), "${item.authorId}${it.attachUrl}"
                                    )
                                },
                                name = item.author,
                                count = item.replies,
                            )
                        }
                    }
                }
            }
//            ExtendedNestedScroll(
//                modifier = Modifier
//                    .padding(top = innerPadding.calculateTopPadding()),
//                header = {
//                    TopicSubjectHeader(
//                        avatar = NetworkModule.NGA_APP_ICON_URL.format(result.fid),
//                        title = result.forumName,
//                        viewModel = viewModel
//                    )
//                }
//            ) {
//
//            }
        }
    }
}

@Composable
fun TopicSubjectHeader(
    avatar: String,
    title: String,
    viewModel: TopicSubjectViewModel,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val (avatarRef, nameRef, expandRef) = createRefs()
        // 图像
        AsyncImage(
            model = avatar.toHttps(),
            contentDescription = "头像",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top, margin = 4.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                },
        )

        // 标题
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(avatarRef.top)
                start.linkTo(avatarRef.end, margin = 4.dp)
            }
        )

        IconButton(
            onClick = {
                viewModel.addCateGoryFavor()
            },
            modifier = Modifier.constrainAs(expandRef) {
                top.linkTo(parent.top)
                end.linkTo(parent.end, margin = 8.dp)
            }
        ) {
            Icon(Icons.Outlined.Grade, contentDescription="收藏")
        }
    }
}