package com.srap.nga.ui.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.srap.nga.utils.throttle

@Composable
fun SearchButton(
    onSearch: () -> Unit
) {
    IconButton(onClick = throttle {
        onSearch()
    }) {
        Icon(Icons.Default.Search, contentDescription = "搜索")
    }
}