package com.srap.nga.ui.userinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.topic.TopicSubjectCard
import com.srap.nga.ui.component.userinfo.UserInfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    id: Int,
    onBackClick: () -> Unit,
    onViewPost: (Int) -> Unit,
) {
    val viewModel = hiltViewModel<UserInfoLoadViewModel, UserInfoLoadViewModel.ViewModelFactory>(key = id.toString()) { factory ->
        factory.create(id)
    }

    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

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
                    Text(
                        text = if (firstVisibleItemIndex > 0) viewModel.result?.username.toString() else "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { innerPadding ->
        RefreshLoadList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            listState = listState,
            content = {
                UserInfoCard(viewModel.result?.username ?: "用户名")
            }
        ) { index, item ->
            TopicSubjectCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable {
                        onViewPost(item.tid)
                    },
                title = item.subject,
                images = item.attachs?.map { NetworkModule.NGA_ATTACHMENTS_URL.format(it.attachUrl) },
                name = item.author,
                count = item.replies,
            )
        }
    }
}