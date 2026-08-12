package com.srap.nga.ui.component.post

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Assert.assertEquals
import org.junit.Test

class CommentBbCodeToolbarTest {

    @Test
    fun insertBbCodeTag_emptySelection_placesCursorBetweenTags() {
        val value = TextFieldValue("你好", selection = TextRange(2))

        val result = value.insertBbCodeTag("[b]", "[/b]")

        assertEquals("你好[b][/b]", result.text)
        assertEquals(TextRange(5), result.selection)
    }

    @Test
    fun insertBbCodeTag_withSelection_wrapsSelectionAndMovesCursorAfter() {
        val value = TextFieldValue("前缀重点后缀", selection = TextRange(2, 4))

        val result = value.insertBbCodeTag("[del]", "[/del]")

        assertEquals("前缀[del]重点[/del]后缀", result.text)
        assertEquals(TextRange(15), result.selection)
    }

    @Test
    fun insertBbCodeTag_reversedSelection_stillWrapsSelectedRange() {
        val value = TextFieldValue("abcdef", selection = TextRange(4, 1))

        val result = value.insertBbCodeTag("[u]", "[/u]")

        assertEquals("a[u]bcd[/u]ef", result.text)
        assertEquals(TextRange(11), result.selection)
    }

    @Test
    fun insertSnippet_insertsAtCursorAndMovesCursorAfter() {
        val value = TextFieldValue("哈哈", selection = TextRange(2))

        val result = value.insertSnippet("[s:ac:茶]")

        assertEquals("哈哈[s:ac:茶]", result.text)
        assertEquals(TextRange(10), result.selection)
    }

    @Test
    fun insertSnippet_replacesSelection() {
        val value = TextFieldValue("前XX后", selection = TextRange(1, 3))

        val result = value.insertSnippet("[s:a2:doge]")

        assertEquals("前[s:a2:doge]后", result.text)
        assertEquals(TextRange(12), result.selection)
    }
}
