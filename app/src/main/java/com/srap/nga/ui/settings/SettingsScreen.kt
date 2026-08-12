package com.srap.nga.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.srap.nga.ui.component.button.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
) {
    val themeMode by AppPreferences.themeMode.collectAsState()
    val dynamicColor by AppPreferences.dynamicColor.collectAsState()
    val forumPostImageCount by AppPreferences.forumPostImageCount.collectAsState()
    val defaultFullscreenInput by AppPreferences.defaultFullscreenInput.collectAsState()
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onBackClick)
                },
                title = {
                    Text("设置")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 32.dp,
            ),
        ) {
            item {
                AppearanceSettingsSection(
                    themeMode = themeMode,
                    dynamicColor = dynamicColor,
                    supportsDynamicColor = supportsDynamicColor,
                    onThemeModeChange = AppPreferences::setThemeMode,
                    onDynamicColorChange = AppPreferences::setDynamicColor,
                )
            }
            item {
                ContentSettingsSection(
                    forumPostImageCount = forumPostImageCount,
                    minImageCount = AppPreferences.MIN_FORUM_POST_IMAGE_COUNT,
                    maxImageCount = AppPreferences.MAX_FORUM_POST_IMAGE_COUNT,
                    onForumPostImageCountChange = AppPreferences::setForumPostImageCount,
                    defaultFullscreenInput = defaultFullscreenInput,
                    onDefaultFullscreenInputChange = AppPreferences::setDefaultFullscreenInput,
                )
            }
            item {
                AboutSettingsSection(versionName = BuildConfig.VERSION_NAME)
            }
        }
    }
}
