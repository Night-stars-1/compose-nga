package com.srap.nga.utils.nga

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.ImagePreviewer
import com.srap.nga.ui.component.card.ExpandableCard
import com.srap.nga.ui.component.webview.HtmlText
import com.srap.nga.utils.nga.parse.NgaContent
import com.srap.nga.utils.nga.parse.SplitQuote
import kotlin.collections.get

object HtmlUtil {
    private val TAG = javaClass.simpleName

    private fun parseNgaHtml(html: String): List<NgaContent> {
        var result = html
        // 去掉一个<br/>
//        result = result.replace("""<br/><br/>""", """<br||||/>""").replace("""<br/>""", "").replace("""<br||||/>""", """<br/>""")
        // [img]https://ngabbs.com/read.php?tid=42886511[/img]
//        result = html.replace(Regex("""\[img](https?://.+?)\[/img]"""), """<img src="$1" onclick="openImage('$1')" />""")
        // [url=http://ngabbs.com/read.php?tid=42886511]2024NGA年度最受欢迎手机游戏[/url]
        result = result.replace(Regex("""\[url=(https?://.+?)](.+?)\[/url]"""), """<a href="$1">$2</a>""")
        // [url]https://bbs.nga.cn/misc/event/20250101genshin/index.html[/url]
        result = result.replace(Regex("""\[url](https?://.+?)\[/url]"""), """<a href="$1">$1</a>""")
        // [tid=23643614]尼尔机械纪元：黑裙白刃的战斗悲歌与横尾作品的阅读体验[/tid]
        result = result.replace(Regex("""\[tid=([0-9]*)](.+?)\[/tid]"""), """<a href="https://bbs.nga.cn/read.php?tid=$1">$2</a>""")
        // [uid=60413566]MissRabbit丶[/uid]
        result = result.replace(Regex("""\[uid=([0-9]*)](.+?)\[/uid]"""), """<a href="https://bbs.nga.cn/nuke.php?func=ucp&uid=$1">$2</a>""")
        // [s:ac:无语]
        result = result.replace(Regex("""\[s:([A-Za-z0-9]+):(.+?)]""")) { matchResult ->
            val key = matchResult.groups[1]?.value // ac
            val value = matchResult.groups[2]?.value // 无语
            val smilePath = smileMap[key]?.get(value)
            """<img src="${NetworkModule.NGA_SMILE_URL.format(smilePath)}" class="smile" />"""
        }
        // [flash]https://www.bilibili.com/video/BV1q16DY1E6M/[/flash]
        result = result.replace(Regex("""\[flash](https?://.+?)\[/flash]"""), """<a href="$1">点击查看视频</a>""")
//        result = result.replace(Regex("""\[(\w+)](.+?)\[/\1]"""), """<$1>$2</$1>""")
        val parseObj = SplitQuote()
        parseObj.splitQuote(result)
        return parseObj.data
    }

    @Composable
    private fun RenderNgaContent(
        ngaContent: List<NgaContent>?,
        uid: String,
        images: List<Pair<String, String>>,
        modifier: Modifier = Modifier,
        onViewPost: (Int) -> Unit,
    ) {
        Column(
            modifier = modifier
        ) {
            ngaContent?.forEach {
                when (it) {
                    is NgaContent.Text -> {
                        if (it.content.isNotBlank() && it.content != "<br/>") {
                            // 排除空格和只有换行符的情况
                            HtmlText(
                                html = it.content,
                                onViewPost = onViewPost,
                                modifier = modifier.fillMaxSize()
                            )
                        }
                    }
                    is NgaContent.Image -> {
                        ImagePreviewer(
                            image = Pair(it.url, "${it.url}$uid"),
                            images = images,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 2.dp),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    is NgaContent.Quote -> {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                it.content.forEach { nestedContent ->
                                    RenderNgaContent(
                                        listOf(nestedContent),
                                        uid,
                                        images,
                                        modifier,
                                        onViewPost
                                    )
                                }
                            }
                        }
                    }
                    is NgaContent.Collapse -> {
                        ExpandableCard(
                            title = it.name
                        ) {
                            Column(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                it.content.forEach { nestedContent ->
                                    RenderNgaContent(
                                        listOf(nestedContent),
                                        uid,
                                        images,
                                        modifier,
                                        onViewPost
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun FromHtml(
        html: String,
        uid: String,
        images: List<String>,
        modifier: Modifier = Modifier,
        onViewPost: (Int) -> Unit,
    ) {
        val ngaContent = parseNgaHtml(html)
        val newImages = images.map { Pair(it, "$it$uid") }
        RenderNgaContent(ngaContent, uid, newImages, modifier, onViewPost)
    }
}
