package com.srap.nga.ui.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ImagesPreviewerTest {
    @Test
    fun `medium thumbnail matches original image and keeps all pages`() {
        val current = "https://img.example.com/a.jpg.medium.jpg" to "a.jpg.medium.jpg-post"
        val images = listOf(
            "https://img.example.com/a.jpg" to "a.jpg-post",
            "https://img.example.com/b.jpg" to "b.jpg-post",
        )

        val preview = prepareImagePreview(current, images)

        assertEquals(2, preview.images.size)
        assertEquals(0, preview.initialIndex)
        assertEquals(preview.images.first().second, preview.currentImage.second)
    }

    @Test
    fun `exact image match opens its position`() {
        val images = listOf(
            "https://img.example.com/a.jpg" to "a-post",
            "https://img.example.com/b.jpg" to "b-post",
            "https://img.example.com/c.jpg" to "c-post",
        )

        val preview = prepareImagePreview(images[1], images)

        assertEquals(3, preview.images.size)
        assertEquals(1, preview.initialIndex)
        assertEquals(preview.images[1].second, preview.currentImage.second)
    }

    @Test
    fun `duplicate image identities receive unique pager keys`() {
        val images = listOf(
            "https://img.example.com/a.jpg" to "duplicate",
            "https://img.example.com/a.jpg" to "duplicate",
        )

        val prepared = preparePreviewImages(images)

        assertNotEquals(prepared[0].second, prepared[1].second)
    }

    @Test
    fun `missing current image falls back to one page`() {
        val current = "https://img.example.com/missing.jpg" to "missing-post"
        val images = listOf(
            "https://img.example.com/a.jpg" to "a-post",
            "https://img.example.com/b.jpg" to "b-post",
        )

        val preview = prepareImagePreview(current, images)

        assertEquals(1, preview.images.size)
        assertEquals(0, preview.initialIndex)
        assertEquals(current.first, preview.currentImage.first)
    }
}
