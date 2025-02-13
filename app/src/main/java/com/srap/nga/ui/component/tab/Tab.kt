package com.srap.nga.ui.component.tab

import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.srap.nga.ui.base.BaseRefreshLoadViewModel

data class SearchResultTag<T>(
    val title: String,
    val viewModel: T
)

@Composable
fun FancyTab(
    title: String,
    onClick: () -> Unit,
    selected: Boolean
) {
    Tab(
        selected = selected,
        onClick = onClick,
        text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    )
}
