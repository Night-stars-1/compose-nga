package com.srap.nga.ui.component.card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 折叠卡片
 */
@Composable
fun ExpandableCard(
    title: String,
    description: String? = null,
    isFillClick: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf (false) }

    Box(
        modifier = Modifier
            .padding(bottom = 2.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(8.dp)
            )
            .animateContentSize()
            .fillMaxWidth()
            .clickable(enabled = isFillClick) {
                expanded = !expanded
            }
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title)
                    if (description != null)
                        Text(
                            description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    if (expanded) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "展开")
                    } else {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "折叠")
                    }
                }
            }
            if (expanded) {
                content()
            }
        }
    }
}