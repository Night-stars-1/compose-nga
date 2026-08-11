package com.srap.nga.utils.nga

import android.util.LruCache
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.srap.nga.ui.component.ImagePreviewer
import com.srap.nga.ui.component.card.ExpandableCard
import com.srap.nga.ui.component.webview.HtmlPoll
import com.srap.nga.ui.component.webview.HtmlTable
import com.srap.nga.ui.component.webview.HtmlText
import com.srap.nga.ui.component.webview.HtmlVideo
import com.srap.nga.utils.nga.parse.NgaContent
import com.srap.nga.utils.nga.parse.NgaMarkupNormalizer
import com.srap.nga.utils.nga.parse.SplitQuote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HtmlUtil {

    private val parsedContentCache = LruCache<String, SplitQuote>(200)

    private fun parseNgaHtml(html: String): SplitQuote {
        val result = NgaMarkupNormalizer.normalizeSizes(
            NgaMarkupNormalizer.normalizeLinks(html)
        )
        val parseObj = SplitQuote()
        parseObj.splitQuote(result)
        return parseObj
    }

    private fun getOrParseNgaHtml(html: String): SplitQuote {
        parsedContentCache.get(html)?.let { return it }
        val parsed = parseNgaHtml(html)
        prewarmTextStyles(parsed.data)
        parsedContentCache.put(html, parsed)
        return parsed
    }

    suspend fun preload(html: String) {
        if (parsedContentCache.get(html) != null) return
        withContext(Dispatchers.Default) {
            getOrParseNgaHtml(html)
        }
    }

    private fun prewarmTextStyles(content: List<NgaContent>) {
        content.forEach {
            when (it) {
                is NgaContent.Text -> NgaMarkupNormalizer.adaptStyles(it.content)
                is NgaContent.Quote -> prewarmTextStyles(it.content)
                is NgaContent.Collapse -> prewarmTextStyles(it.content)
                else -> Unit
            }
        }
    }

    @Composable
    private fun RenderNgaContent(
        ngaContent: List<NgaContent>?,
        uid: String,
        images: List<Pair<String, String>>,
        modifier: Modifier = Modifier,
        onViewPost: (Int) -> Unit,
        openUrl: (String) -> Unit,
        onViewPostByPid: ((Int) -> Unit)? = null,
        quoteDepth: Int = 0,
    ) {
        Column(modifier = modifier) {
            ngaContent?.forEach {
                when (it) {
                    is NgaContent.Text -> {
                        if (it.content.isNotBlank() && it.content != "<br/>") {
                            HtmlText(
                                html = it.content,
                                modifier = Modifier.fillMaxWidth(),
                                onViewPost = onViewPost,
                                openUrl = openUrl,
                                onViewPostByPid = onViewPostByPid,
                            )
                        }
                    }
                    is NgaContent.Image -> {
                        ImagePreviewer(
                            image = Pair(it.url, "${it.url}$uid"),
                            images = images,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 2.dp),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    is NgaContent.Video -> {
                        HtmlVideo(
                            video = it.video,
                            modifier = Modifier.padding(vertical = 4.dp),
                            openUrl = openUrl,
                        )
                    }
                    is NgaContent.Quote -> {
                        val isNestedQuote = quoteDepth > 0
                        val containerColor = if (isNestedQuote) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                        val contentColor = if (isNestedQuote) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                        val accentColor = if (isNestedQuote) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }

                        Surface(
                            modifier = Modifier
                                .padding(vertical = if (isNestedQuote) 6.dp else 4.dp)
                                .fillMaxWidth(),
                            shape = if (isNestedQuote) {
                                MaterialTheme.shapes.small
                            } else {
                                MaterialTheme.shapes.medium
                            },
                            color = containerColor,
                            contentColor = contentColor,
                            tonalElevation = if (isNestedQuote) 0.dp else 1.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .drawBehind {
                                        val barWidth = 4.dp.toPx()
                                        drawRoundRect(
                                            color = accentColor,
                                            size = Size(barWidth, size.height),
                                            cornerRadius = CornerRadius(barWidth / 2f),
                                        )
                                    }
                                    .padding(
                                        start = 16.dp,
                                        end = 12.dp,
                                        top = 10.dp,
                                        bottom = 10.dp,
                                    )
                                    .fillMaxWidth()
                            ) {
                                RenderNgaContent(
                                    ngaContent = it.content,
                                    uid = uid,
                                    images = images,
                                    modifier = Modifier.fillMaxWidth(),
                                    onViewPost = onViewPost,
                                    openUrl = openUrl,
                                    onViewPostByPid = onViewPostByPid,
                                    quoteDepth = quoteDepth + 1,
                                )
                            }
                        }
                    }
                    is NgaContent.Collapse -> {
                        ExpandableCard(title = it.name) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                RenderNgaContent(
                                    ngaContent = it.content,
                                    uid = uid,
                                    images = images,
                                    modifier = Modifier.fillMaxWidth(),
                                    onViewPost = onViewPost,
                                    openUrl = openUrl,
                                    onViewPostByPid = onViewPostByPid,
                                    quoteDepth = quoteDepth,
                                )
                            }
                        }
                    }
                    is NgaContent.Table -> {
                        HtmlTable(
                            table = it.table,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onViewPost = onViewPost,
                            openUrl = openUrl,
                            onViewPostByPid = onViewPostByPid,
                        )
                    }
                    is NgaContent.Poll -> {
                        HtmlPoll(
                            poll = it.poll,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun FromHtml(
        html: String,
        uid: String,
        modifier: Modifier = Modifier,
        onViewPost: (Int) -> Unit,
        openUrl: (String) -> Unit,
        onViewPostByPid: ((Int) -> Unit)? = null,
    ) {
        val cachedData = remember(html) { parsedContentCache.get(html) }
        val data by produceState(initialValue = cachedData, key1 = html) {
            if (value == null) {
                value = withContext(Dispatchers.Default) {
                    getOrParseNgaHtml(html)
                }
            }
        }
        val newImages = remember(data, uid) {
            data?.imageList?.map { Pair(it, "$it$uid") }.orEmpty()
        }
        RenderNgaContent(
            ngaContent = data?.data,
            uid = uid,
            images = newImages,
            modifier = modifier,
            onViewPost = onViewPost,
            openUrl = openUrl,
            onViewPostByPid = onViewPostByPid,
        )
    }
}
