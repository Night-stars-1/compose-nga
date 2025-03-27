package com.srap.nga.ui.fav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.list.RefreshLoadList
import com.srap.nga.ui.component.topic.TopicSubjectCard

/**
 * 收藏夹内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteContentScreen(
    id: Int,
    title: String,
    onViewPost: (id: Int) -> Unit,
    onBackClick: (() -> Unit)?,
) {
    val viewModel: FavContentViewModel = hiltViewModel<FavContentViewModel, FavContentViewModel.ViewModelFactory>(key = "${id}favorite"){ factory ->
        factory.create(id)
    }
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
                    if (onBackClick != null) BackButton { onBackClick() }
                },
                title = {
                    Text(title)
                }
            )
        }
    ) { innerPadding ->
        RefreshLoadList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            items(viewModel.list) {
                TopicSubjectCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable {
                            onViewPost(it.tid)
                        },
                    title = it.subject,
                    name = it.author,
                    count = it.replies,
                )
            }
        }
    }
}
