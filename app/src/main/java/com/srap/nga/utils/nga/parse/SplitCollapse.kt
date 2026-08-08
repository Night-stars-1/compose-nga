package com.srap.nga.utils.nga.parse

import com.srap.nga.constant.Constants.EMPTY_STRING
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class SplitCollapse {
    private val dataStack = mutableListOf<NgaContent>()
    val imageList = mutableListOf<String>()

    private fun splitLeaf(text: String): List<NgaContent>? {
        val splitTable = SplitTable()
        val content = splitTable.splitTable(text)
        imageList.addAll(splitTable.imageList)
        return content
    }

    fun splitCollapse(text: String): List<NgaContent>? {
        val normalizedText = HtmlTableParser.normalizeForHtmlParser(text)
        val doc: Document = Jsoup.parse(normalizedText)
        // Keep source markup compact so Jsoup indentation does not become visible spacing.
        doc.outputSettings().prettyPrint(false)
        val body = doc.select("body").first()
        if (body == null) {
            return null
        }
        val bodyChildren = body.childNodes()
        var pendingHtml = EMPTY_STRING

        for (child in bodyChildren) {
            if (child is Element) {
                if (child.hasClass("foldSnippet")) {
                    splitLeaf(pendingHtml)?.let { dataStack.addAll(it) }
                    pendingHtml = EMPTY_STRING
                    val titleObj = child.select(".foldTxt").first()
                    val contentObj = child.select(".foldHidden").first()
                    val title = titleObj?.text()?.trim() ?: "异常标题"
                    val content = contentObj?.html() ?: "异常内容"
                    val collapseContent = SplitCollapse().splitCollapse(content)
                    collapseContent?.let {
                        dataStack.add(NgaContent.Collapse(title, it))
                    }
                } else if (child.hasClass("voteBlock")) {
                    splitLeaf(pendingHtml)?.let { dataStack.addAll(it) }
                    pendingHtml = EMPTY_STRING
                    val poll = HtmlPollParser.parse(child)
                    if (poll != null) {
                        dataStack.add(NgaContent.Poll(poll))
                    } else {
                        pendingHtml += child.outerHtml()
                    }
                } else if (child.normalName() == "video" || child.hasClass("video")) {
                    val video = HtmlVideoParser.parse(child)
                    if (video != null) {
                        splitLeaf(pendingHtml)?.let { dataStack.addAll(it) }
                        pendingHtml = EMPTY_STRING
                        val gifImageUrl = HtmlVideoParser.gifImageUrl(video)
                        if (gifImageUrl != null) {
                            imageList.add(gifImageUrl)
                            dataStack.add(NgaContent.Image(gifImageUrl))
                        } else {
                            dataStack.add(NgaContent.Video(video))
                        }
                    } else {
                        pendingHtml += child.outerHtml()
                    }
                } else if (child.nodeName() != "br") {
                    pendingHtml += child.outerHtml()
                } else {
                    val content = splitLeaf(pendingHtml)
                    pendingHtml = EMPTY_STRING
                    content?.let { dataStack.addAll(it) }
                }
            } else {
                pendingHtml += child.outerHtml()
            }
        }

        val content = splitLeaf(pendingHtml)
        content?.let { dataStack.addAll(it) }

        return dataStack
    }
}
