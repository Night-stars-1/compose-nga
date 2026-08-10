package com.srap.nga.logic.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SearchHistoryStoreTest {
    @Test
    fun normalizeQuery_trimsAndRejectsBlankText() {
        assertEquals("NGA", normalizeSearchQuery("  NGA  "))
        assertNull(normalizeSearchQuery("   "))
    }

    @Test
    fun addHistory_placesNewestQueryFirst() {
        val result = addSearchHistory(
            entries = listOf("旧搜索", "更早的搜索"),
            query = "新搜索",
        )

        assertEquals(listOf("新搜索", "旧搜索", "更早的搜索"), result)
    }

    @Test
    fun addHistory_promotesDuplicateIgnoringCase() {
        val result = addSearchHistory(
            entries = listOf("NGA", "Compose"),
            query = " compose ",
        )

        assertEquals(listOf("compose", "NGA"), result)
    }

    @Test
    fun normalizeHistory_removesInvalidEntriesAndHonorsLimit() {
        val result = normalizeSearchHistory(
            entries = listOf(" one ", "", "ONE", "two", "three"),
            maxSize = 2,
        )

        assertEquals(listOf("one", "two"), result)
    }

    @Test
    fun removeHistory_matchesNormalizedTextIgnoringCase() {
        val result = removeSearchHistory(
            entries = listOf("NGA", "Compose", "Kotlin"),
            query = " compose ",
        )

        assertEquals(listOf("NGA", "Kotlin"), result)
    }
}
