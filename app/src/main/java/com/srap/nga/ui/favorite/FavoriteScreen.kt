package com.srap.nga.ui.favorite

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.srap.nga.ui.component.card.ActionTextCard
import com.srap.nga.ui.component.list.RefreshLoadList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onViewFavoriteContent: (Int, String) -> Unit,
    onBackClick: (() -> Unit)?,
) {
    val viewModel: FavoriteViewModel = hiltViewModel()

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
                    Text("收藏夹")
                }
            )
        }
    ) { innerPadding ->
        RefreshLoadList(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(top = 4.dp),
        ) { item ->
            ActionTextCard(
                title = item.name,
                description = "${item.length}条内容",
                isFillClick = true,
                onClick = {
                    onViewFavoriteContent(item.id, item.name)
                },
                icon = { Icon(Icons.AutoMirrored.Outlined.NavigateNext, contentDescription = "前往") }
            )
        }
    }
}
