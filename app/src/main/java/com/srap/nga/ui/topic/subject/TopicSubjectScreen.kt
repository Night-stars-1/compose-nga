package com.srap.nga.ui.topic.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.srap.nga.logic.preferences.AppPreferences
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
    val forumPostImageCount by AppPreferences.forumPostImageCount.collectAsState()
    val viewModel = hiltViewModel<TopicSubjectViewModel, TopicSubjectViewModel.ViewModelFactory>(key = id.toString()) { factory ->
        factory.create(id)
    }
    val result = viewModel.result

    val scope = rememberCoroutineScope()
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
            LoadingCard(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            )
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
                        factory.create(
                            id = id,
                            list = data.data,
                            totalPage = result.totalPage,
                            attachPrefix = data.attachPrefix,
                        )
                    }
                )
            ) + data.subForum.mapIndexed { index, item ->
                SearchResultTag(
                    title = item.name,
                    viewModel = hiltViewModel<TopicSubjectLoadViewModel, TopicSubjectLoadViewModel.ViewModelFactory>(key = "${id}load${index}") { factory ->
                        factory.create(id = item.id)
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                SecondaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
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

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) { index ->
                    val viewModel = loadViewModelList[index].viewModel
                    val prefix = viewModel.attachPrefix.ifBlank { data.attachPrefix }
                    RefreshLoadList(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 8.dp,
                            bottom = 64.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = viewModel.list,
                            key = { item -> item.tid },
                        ) { item ->
                            TopicSubjectCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onViewPost(item.tid)
                                    },
                                title = item.subject,
                                images = item.attachs?.map {
                                    Pair(
                                        attachmentUrl(
                                            prefix = prefix,
                                            path = it.attachUrl,
                                        ),
                                        "${item.authorId}${it.attachUrl}",
                                    )
                                },
                                name = item.author,
                                count = item.replies,
                                maxImageCount = forumPostImageCount,
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

private fun attachmentUrl(prefix: String, path: String): String {
    val normalizedPath = path.trimStart('/')
    val normalizedPrefix = prefix.trim().trimEnd('/')
    val absolutePrefix = when {
        normalizedPrefix.startsWith("//") -> "https:" + normalizedPrefix
        normalizedPrefix.startsWith("http://", ignoreCase = true) ->
            "https://" + normalizedPrefix.substringAfter("://")
        normalizedPrefix.startsWith("https://", ignoreCase = true) -> normalizedPrefix
        else -> NetworkModule.NGA_ATTACHMENTS_URL.substringBeforeLast('/')
    }.trimEnd('/')

    return absolutePrefix + "/" + normalizedPath
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
