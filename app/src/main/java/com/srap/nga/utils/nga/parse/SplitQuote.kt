package com.srap.nga.utils.nga.parse

class SplitQuote {
    val imageList = mutableListOf<String>()

    private val dataStack = mutableListOf<NgaContent>()
    val data get() = dataStack.toList()

    private val startKey = "[quote]"
    private val endKey = "[/quote]"

    fun splitQuote(text: String, start: Int = 0): List<NgaContent>? {
        val source = text.substring(start.coerceIn(0, text.length))
        var cursor = 0

        while (cursor < source.length) {
            val quoteStart = source.indexOf(startKey, cursor)
            if (quoteStart == -1) {
                appendText(source.substring(cursor))
                break
            }

            val quoteEnd = findMatchingQuoteEnd(source, quoteStart)
            if (quoteEnd == -1) {
                appendText(source.substring(cursor))
                break
            }

            appendText(source.substring(cursor, quoteStart))

            val nestedParser = SplitQuote()
            val quoteContent = nestedParser.splitQuote(
                source.substring(quoteStart + startKey.length, quoteEnd)
            )
            imageList.addAll(nestedParser.imageList)
            quoteContent?.takeIf { it.isNotEmpty() }?.let {
                dataStack.add(NgaContent.Quote(it, "quote"))
            }

            cursor = quoteEnd + endKey.length
        }

        return dataStack.takeIf { it.isNotEmpty() }
    }

    private fun appendText(text: String) {
        if (text.isEmpty()) return

        val parser = SplitCollapse()
        parser.splitCollapse(text)?.let(dataStack::addAll)
        imageList.addAll(parser.imageList)
    }

    private fun findMatchingQuoteEnd(text: String, quoteStart: Int): Int {
        var depth = 1
        var cursor = quoteStart + startKey.length

        while (cursor < text.length) {
            val nextStart = text.indexOf(startKey, cursor)
            val nextEnd = text.indexOf(endKey, cursor)
            if (nextEnd == -1) return -1

            if (nextStart != -1 && nextStart < nextEnd) {
                depth += 1
                cursor = nextStart + startKey.length
            } else {
                depth -= 1
                if (depth == 0) return nextEnd
                cursor = nextEnd + endKey.length
            }
        }

        return -1
    }
}
