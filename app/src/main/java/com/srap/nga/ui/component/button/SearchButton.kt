package com.srap.nga.ui.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import com.srap.nga.utils.throttle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchButton(
    onSearch: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text("搜索") } },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = throttle {
            onSearch()
        }) {
            Icon(Icons.Default.Search, contentDescription = "搜索")
        }
    }

}