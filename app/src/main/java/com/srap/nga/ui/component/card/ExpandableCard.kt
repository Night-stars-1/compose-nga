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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf (false) }

    Box(
        modifier = Modifier
            .padding(bottom = 2.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .animateContentSize()
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(8.dp)
                )

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