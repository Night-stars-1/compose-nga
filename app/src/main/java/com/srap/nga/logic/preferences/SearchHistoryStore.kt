package com.srap.nga.logic.preferences

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal const val MAX_SEARCH_HISTORY_SIZE = 20

internal fun normalizeSearchQuery(query: String): String? =
    query.trim().takeIf { it.isNotEmpty() }

internal fun normalizeSearchHistory(
    entries: List<String>,
    maxSize: Int = MAX_SEARCH_HISTORY_SIZE,
): List<String> {
    if (maxSize <= 0) return emptyList()

    val normalizedEntries = mutableListOf<String>()
    for (entry in entries) {
        val normalizedEntry = normalizeSearchQuery(entry) ?: continue
        if (normalizedEntries.none { it.equals(normalizedEntry, ignoreCase = true) }) {
            normalizedEntries += normalizedEntry
        }
        if (normalizedEntries.size == maxSize) break
    }
    return normalizedEntries
}

internal fun addSearchHistory(
    entries: List<String>,
    query: String,
    maxSize: Int = MAX_SEARCH_HISTORY_SIZE,
): List<String> {
    val normalizedQuery = normalizeSearchQuery(query)
        ?: return normalizeSearchHistory(entries, maxSize)
    return normalizeSearchHistory(listOf(normalizedQuery) + entries, maxSize)
}

internal fun removeSearchHistory(
    entries: List<String>,
    query: String,
): List<String> {
    val normalizedQuery = normalizeSearchQuery(query)
        ?: return normalizeSearchHistory(entries)
    return normalizeSearchHistory(
        entries.filterNot { it.trim().equals(normalizedQuery, ignoreCase = true) },
    )
}

@Singleton
class SearchHistoryStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val gson = Gson()
    private val historyType = object : TypeToken<List<String?>>() {}.type
    private val _history = MutableStateFlow(loadHistory())

    val history = _history.asStateFlow()

    @Synchronized
    fun record(query: String): String? {
        val normalizedQuery = normalizeSearchQuery(query) ?: return null
        persist(addSearchHistory(_history.value, normalizedQuery))
        return normalizedQuery
    }

    @Synchronized
    fun remove(query: String) {
        persist(removeSearchHistory(_history.value, query))
    }

    @Synchronized
    fun clear() {
        if (_history.value.isEmpty()) return

        _history.value = emptyList()
        preferences.edit { remove(HISTORY_KEY) }
    }

    private fun loadHistory(): List<String> {
        val json = preferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val storedHistory = runCatching {
            gson.fromJson<List<String?>>(json, historyType)
        }.getOrNull().orEmpty().filterNotNull()
        return normalizeSearchHistory(storedHistory)
    }

    private fun persist(entries: List<String>) {
        if (entries == _history.value) return

        _history.value = entries
        preferences.edit { putString(HISTORY_KEY, gson.toJson(entries)) }
    }

    private companion object {
        const val PREFERENCES_NAME = "searchHistory"
        const val HISTORY_KEY = "queries"
    }
}
