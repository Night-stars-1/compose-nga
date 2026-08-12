package com.srap.nga.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSettingsSection(
    versionName: String,
    isCheckingUpdate: Boolean,
    onCheckUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingsSectionTitle("关于")
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
            supportingContent = {
                Text(versionName)
            },
            modifier = Modifier.padding(bottom = ListItemDefaults.SegmentedGap),
        ) {
            Text("版本")
        }
        SegmentedListItem(
            onClick = onCheckUpdate,
            shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
            supportingContent = {
                Text(
                    if (isCheckingUpdate) {
                        "正在检查..."
                    } else {
                        "从 GitHub 获取最新版本"
                    }
                )
            },
        ) {
            Text("检查更新")
        }
    }
}
