package com.srap.nga.utils.nga.parse

import com.srap.nga.constant.Constants.EMPTY_STRING
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class SplitCollapse {
    private val dataStack = mutableListOf<NgaContent>()
    val imageList = mutableListOf<String>()

    fun splitCollapse(text: String): List<NgaContent>? {
        val doc: Document = Jsoup.parse(text)
        val body = doc.select("body").first()
        if (body == null) {
            return null
        }
        val bodyChildren = body.childNodes()
        var text = EMPTY_STRING

        for (child in bodyChildren) {
            if (child is Element) {
                if (child.hasClass("foldSnippet")) {
                    // 获取标题和内容部分
                    val titleObj = child.select(".foldTxt").first()
                    val contentObj = child.select(".foldHidden").first()
                    val title = titleObj?.text()?.trim() ?: "异常标题"
                    val content = contentObj?.html() ?: "异常内容"
                    val collapseContent = SplitCollapse().splitCollapse(content)
                    collapseContent?.let {
                        dataStack.add(NgaContent.Collapse(title, it))
                    }
                } else if (child.nodeName() != "br") {
                    text += child.outerHtml()
                } else {
                    val content = SplitImage().splitImage(text)
                    text = EMPTY_STRING
                    content?.let {
                        it.forEach { item ->
                            if (item is NgaContent.Image) {
                                imageList.add(item.url)
                            }
                        }
                        dataStack.addAll(it)
                    }
                }
            } else {
//                val newText = child.outerHtml()
//                val content = SplitImage().splitImage(newText)
//                content?.let { dataStack.addAll(it) }
                text += child.outerHtml()
            }
        }

        // 添加结尾没有<br>导致未解析的内容
        val content = SplitImage().splitImage(text)
        content?.let {
            it.forEach { item ->
                if (item is NgaContent.Image) {
                    imageList.add(item.url)
                }
            }
            dataStack.addAll(it)
        }

        return dataStack
    }
}