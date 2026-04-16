package com.srap.nga.ui.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 可展开的悬浮操作按钮
 * @param expanded 是否展开
 * @param onExpandedChange 展开状态变化回调
 * @param items 子按钮列表
 * @param containerColor 主按钮背景色
 * @param contentColor 主按钮内容色
 */
@Composable
fun ExpandableFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<ExpandableFabItem>,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "fab_rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 子按钮列表
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEach { item ->
                    FabItem(
                        item = item,
                        onClick = {
                            item.onClick()
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }

        // 主 FAB 按钮
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) },
            containerColor = containerColor,
            contentColor = contentColor
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (expanded) "收起" else "展开",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun FabItem(
    item: ExpandableFabItem,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.label.isNotEmpty()) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = item.containerColor,
            contentColor = item.contentColor,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label
            )
        }
    }
}

/**
 * 可展开 FAB 的子按钮数据类
 */
data class ExpandableFabItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String = "",
    val onClick: () -> Unit,
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified
)
