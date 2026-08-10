package com.srap.nga.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingsSectionTitle("关于")
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
            supportingContent = {
                Text(versionName)
            },
        ) {
            Text("版本")
        }
    }
}
