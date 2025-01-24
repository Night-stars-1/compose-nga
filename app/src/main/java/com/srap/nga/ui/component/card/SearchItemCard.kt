package com.srap.nga.ui.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SearchItemCard(
    title: String,
    startIcon: ImageVector,
    endIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Icon(
                imageVector = startIcon,
                contentDescription = "头部图标"
            )
            Text(
               text = title,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }

        Icon(
            imageVector = endIcon,
            contentDescription = "尾部图标"
        )
    }
}