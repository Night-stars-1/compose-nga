package com.srap.nga.ui.userinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.topic.TopicSubjectCard
import com.srap.nga.ui.component.userinfo.UserInfoCard
import com.srap.nga.utils.StorageUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    id: Int,
    isLoggedIn: Boolean,
    onViewPost: (Int) -> Unit,
    onViewLogin: () -> Unit,
    onBackClick: (() -> Unit)?,
    onViewFavorite: () -> Unit = {},
    onViewSettings: () -> Unit = {},
) {
    val viewModel =
        hiltViewModel<UserInfoLoadViewModel, UserInfoLoadViewModel.ViewModelFactory>(key = id.toString()) { factory ->
            factory.create(id)
        }

    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

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
                    if (onBackClick != null) BackButton { onBackClick() }
                },
                title = {
                    Text(
                        text = if (onBackClick != null) {
                            if (firstVisibleItemIndex > 0) {
                                viewModel.result?.username ?: "用户详情"
                            } else "用户详情"
                        } else "我的",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable {
                                if (firstVisibleItemIndex > 0) {
                                    scope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                            }
                    )
                },
                actions = {
                    if (onBackClick == null) {
                        IconButton(onClick = onViewFavorite) {
                            Icon(Icons.Outlined.Grade, contentDescription = "收藏")
                        }
                        IconButton(onClick = onViewSettings) {
                            Icon(Icons.Outlined.Settings, contentDescription = "设置")
                        }
                    }
                },
            )
        }
    ) { innerPadding ->
        if (!isLoggedIn) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FilledTonalButton(
                    modifier = Modifier.widthIn(min = 120.dp),
                    onClick = onViewLogin,
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = null)
                    Text("登录")
                }
            }
        } else {
            RefreshLoadList(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                listState = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item {
                    val result = viewModel.result
                    if (onBackClick == null) {
                        if (result != null) {
                            MyProfileHeader(result = result)
                        } else {
                            MyProfileHeaderPlaceholder(
                                uid = id,
                                name = viewModel.list.firstOrNull()?.author,
                                isLoading = viewModel.isUserInfoLoading,
                            )
                        }
                    } else if (result != null) {
                        UserInfoCard(
                            avatar = result.avatar,
                            name = result.username,
                            description = "级别: ${result.group} | IP属地: ${result.ipLoc}\n" +
                                    "UID: ${result.uid} | 威望: ${result.rvrc}",
                            isFollowing = result.follow != 0,
                            followLoading = viewModel.isFollowLoading,
                            onFollowClick = if (result.uid != StorageUtils.Uid) {
                                viewModel::toggleFollow
                            } else {
                                null
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                items(
                    items = viewModel.list,
                    key = { it.tid },
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
                                NetworkModule.NGA_ATTACHMENTS_URL.format(it.attachUrl),
                                "${item.authorId}${it.attachUrl}"
                            )
                        },
                        name = item.author,
                        count = item.replies,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
