package com.srap.nga.ui.component.webview

import androidx.core.text.HtmlCompat
import org.junit.Assert.assertEquals
import org.junit.Test

class NgaWebViewTest {
    @Test
    fun `uses compact separators for adjacent list items`() {
        assertEquals(
            HtmlCompat.FROM_HTML_MODE_COMPACT,
            NGA_HTML_PARSE_FLAGS and HtmlCompat.FROM_HTML_MODE_COMPACT,
        )
    }
}
