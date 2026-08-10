package com.srap.nga.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.srap.nga.logic.preferences.AppThemeMode

private data class ThemeModeOption(
    val mode: AppThemeMode,
    val label: String,
)

private val themeModeOptions = listOf(
    ThemeModeOption(AppThemeMode.System, "系统"),
    ThemeModeOption(AppThemeMode.Light, "浅色"),
    ThemeModeOption(AppThemeMode.Dark, "深色"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsSection(
    themeMode: AppThemeMode,
    dynamicColor: Boolean,
    supportsDynamicColor: Boolean,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dynamicColorChecked = supportsDynamicColor && dynamicColor

    Column(modifier = modifier.fillMaxWidth()) {
        SettingsSectionTitle("外观")

        Column(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        ) {
            SegmentedListItem(
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
            ) {
                Column {
                    Text(
                        text = "主题模式",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "设置应用的明暗外观",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        themeModeOptions.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = themeMode == option.mode,
                                onClick = {
                                    onThemeModeChange(option.mode)
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = themeModeOptions.size,
                                ),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(option.label)
                            }
                        }
                    }
                }
            }

            SegmentedListItem(
                onClick = {
                    onDynamicColorChange(!dynamicColor)
                },
                shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                enabled = supportsDynamicColor,
                modifier = Modifier.semantics {
                    role = Role.Switch
                    toggleableState = ToggleableState(dynamicColorChecked)
                },
                supportingContent = {
                    Text(
                        if (supportsDynamicColor) {
                            "根据系统壁纸调整应用配色"
                        } else {
                            "需要 Android 12 或更高版本"
                        }
                    )
                },
                trailingContent = {
                    Switch(
                        checked = dynamicColorChecked,
                        onCheckedChange = null,
                        enabled = supportsDynamicColor,
                        thumbContent = {
                            if (dynamicColorChecked) {
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
                Text("动态取色")
            }
        }
    }
}
