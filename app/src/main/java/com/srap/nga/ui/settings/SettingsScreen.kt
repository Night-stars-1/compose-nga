package com.srap.nga.ui.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.srap.nga.BuildConfig
import com.srap.nga.logic.preferences.AppPreferences
import com.srap.nga.logic.preferences.AppThemeMode
import com.srap.nga.ui.component.button.BackButton
import kotlin.math.roundToInt

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
fun SettingsScreen(
    onBackClick: () -> Unit,
) {
    val themeMode by AppPreferences.themeMode.collectAsState()
    val dynamicColor by AppPreferences.dynamicColor.collectAsState()
    val forumPostImageCount by AppPreferences.forumPostImageCount.collectAsState()
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onBackClick)
                },
                title = {
                    Text("设置")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                SettingsSectionTitle("外观")
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = "主题模式",
                        style = MaterialTheme.typography.titleMedium,
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
                                    AppPreferences.setThemeMode(option.mode)
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

            item {
                ListItem(
                    supportingContent = {
                        Text(
                            if (supportsDynamicColor) {
                                "根据系统壁纸调整应用配色"
                            } else {
                                "需要 Android 12 或更高版本"
                            }
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = supportsDynamicColor && dynamicColor,
                            onCheckedChange = null,
                            enabled = supportsDynamicColor,
                        )
                    },
                    modifier = Modifier.clickable(enabled = supportsDynamicColor) {
                        AppPreferences.setDynamicColor(!dynamicColor)
                    },
                ) {
                    Text("动态取色")
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }

            item {
                SettingsSectionTitle("内容")
            }

            item {
                Column {
                    ListItem(
                        supportingContent = {
                            Text("设置板块列表中每个帖子最多显示的缩略图数量")
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                            )
                        },
                        trailingContent = {
                            Text(
                                text = if (forumPostImageCount == 0) {
                                    "关闭"
                                } else {
                                    "$forumPostImageCount 张"
                                },
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                    ) {
                        Text("板块帖子图片")
                    }
                    Slider(
                        value = forumPostImageCount.toFloat(),
                        onValueChange = {
                            AppPreferences.setForumPostImageCount(it.roundToInt())
                        },
                        valueRange = AppPreferences.MIN_FORUM_POST_IMAGE_COUNT.toFloat()..
                            AppPreferences.MAX_FORUM_POST_IMAGE_COUNT.toFloat(),
                        steps = AppPreferences.MAX_FORUM_POST_IMAGE_COUNT -
                            AppPreferences.MIN_FORUM_POST_IMAGE_COUNT - 1,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }

            item {
                SettingsSectionTitle("关于")
            }

            item {
                ListItem(
                    supportingContent = {
                        Text(BuildConfig.VERSION_NAME)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                        )
                    },
                ) {
                    Text("版本")
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}
