package com.srap.nga.logic.preferences

import android.content.Context
import androidx.core.content.edit
import com.srap.nga.myApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    System,
    Light,
    Dark;

    companion object {
        fun fromStoredValue(value: String?): AppThemeMode =
            entries.firstOrNull { it.name == value } ?: System
    }
}

object AppPreferences {
    const val MIN_FORUM_POST_IMAGE_COUNT = 0
    const val MAX_FORUM_POST_IMAGE_COUNT = 7

    private const val PreferencesName = "appSettings"
    private const val ThemeModeKey = "themeMode"
    private const val DynamicColorKey = "dynamicColor"
    private const val ForumPostImageCountKey = "forumPostImageCount"
    private const val DefaultFullscreenInputKey = "defaultFullscreenInput"

    private val preferences by lazy {
        myApplication.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
    }

    private val _themeMode by lazy {
        MutableStateFlow(
            AppThemeMode.fromStoredValue(preferences.getString(ThemeModeKey, null))
        )
    }
    val themeMode by lazy { _themeMode.asStateFlow() }

    private val _dynamicColor by lazy {
        MutableStateFlow(preferences.getBoolean(DynamicColorKey, true))
    }
    val dynamicColor by lazy { _dynamicColor.asStateFlow() }

    private val _forumPostImageCount by lazy {
        MutableStateFlow(
            preferences.getInt(ForumPostImageCountKey, MAX_FORUM_POST_IMAGE_COUNT)
                .coerceIn(MIN_FORUM_POST_IMAGE_COUNT, MAX_FORUM_POST_IMAGE_COUNT)
        )
    }
    val forumPostImageCount by lazy { _forumPostImageCount.asStateFlow() }

    private val _defaultFullscreenInput by lazy {
        MutableStateFlow(preferences.getBoolean(DefaultFullscreenInputKey, false))
    }
    val defaultFullscreenInput by lazy { _defaultFullscreenInput.asStateFlow() }

    fun setThemeMode(value: AppThemeMode) {
        _themeMode.value = value
        preferences.edit { putString(ThemeModeKey, value.name) }
    }

    fun setDynamicColor(value: Boolean) {
        _dynamicColor.value = value
        preferences.edit { putBoolean(DynamicColorKey, value) }
    }

    fun setDefaultFullscreenInput(value: Boolean) {
        _defaultFullscreenInput.value = value
        preferences.edit { putBoolean(DefaultFullscreenInputKey, value) }
    }

    fun setForumPostImageCount(value: Int) {
        val normalized = value.coerceIn(
            MIN_FORUM_POST_IMAGE_COUNT,
            MAX_FORUM_POST_IMAGE_COUNT,
        )
        if (_forumPostImageCount.value == normalized) return

        _forumPostImageCount.value = normalized
        preferences.edit { putInt(ForumPostImageCountKey, normalized) }
    }
}
