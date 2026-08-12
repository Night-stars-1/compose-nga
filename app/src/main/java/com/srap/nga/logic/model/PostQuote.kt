package com.srap.nga.logic.model

/**
 * 构造 NGA 评论回复所需的引用正文。
 *
 * NGA 的 quote 接口要求把原楼内容包在 quote 标签中，再追加本次回复内容。
 */
internal fun buildNgaQuoteContent(
    post: PostResponse.Result,
    threadId: Int,
    replyContent: String,
): String {
    val quoteHeader =
        "[quote][pid=${post.pid},$threadId,${post.lou}]Reply[/pid] " +
            "[b]Post by [uid=${post.author.uid}]${post.author.username}[/uid] " +
            "(${post.postDate}):[/b]"
    return "$quoteHeader\n\n${post.content}[/quote]\n\n${replyContent.trim()}"
}
