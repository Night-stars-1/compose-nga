package com.srap.nga.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentSettingsSection(
    forumPostImageCount: Int,
    minImageCount: Int,
    maxImageCount: Int,
    onForumPostImageCountChange: (Int) -> Unit,
    defaultFullscreenInput: Boolean,
    onDefaultFullscreenInputChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingsSectionTitle("内容")

        Column(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            SegmentedListItem(
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "板块帖子图片",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = "设置板块列表中每个帖子最多显示的缩略图数量",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = if (forumPostImageCount == 0) {
                                "关闭"
                            } else {
                                "$forumPostImageCount 张"
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                    Slider(
                        value = forumPostImageCount.toFloat(),
                        onValueChange = {
                            onForumPostImageCountChange(it.roundToInt())
                        },
                        valueRange = minImageCount.toFloat()..maxImageCount.toFloat(),
                        steps = maxImageCount - minImageCount - 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    )
                }
            }

            SegmentedListItem(
                onClick = {
                    onDefaultFullscreenInputChange(!defaultFullscreenInput)
                },
                shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                modifier = Modifier.semantics {
                    role = Role.Switch
                    toggleableState = ToggleableState(defaultFullscreenInput)
                },
                supportingContent = {
                    Text("打开回复框时直接进入全屏编辑模式")
                },
                trailingContent = {
                    Switch(
                        checked = defaultFullscreenInput,
                        onCheckedChange = null,
                        thumbContent = {
                            if (defaultFullscreenInput) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        },
                    )
                },
            ) {
                Text("默认全屏输入")
            }
        }
    }
}
