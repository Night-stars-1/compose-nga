package com.srap.nga.utils.nga.parse

import android.util.Log

private const val TAG = "SplitQuote"

class SplitQuote {

    val imageList = mutableListOf<String>()

    private val stack = mutableListOf<Int>()
    private val _dataStack = mutableListOf<NgaContent>()
    val data get() = _dataStack.toList()
    private val startKey = "[quote]"
    private val startKeyLength = startKey.length
    private val endKey = "[/quote]"
    private val endKeyLength = endKey.length

    fun splitQuote(text: String, start: Int = 0): List<NgaContent>? {
        try {
            var currentStart = start

            while (true) {
                // 起始关键词位置
                val startKeyIndex = text.indexOf(startKey, currentStart)
                // 结束关键词位置
                val endKeyIndex = text.indexOf(endKey, currentStart)
                if (currentStart == 0) {
                    // aaa[quote]bbb[/quote]ccc
                    // 如果起始搜索位置为 0，则向数据堆栈添加关键词之前的内容 -> aaa
                    // 该内容对进行下一步切割
                    val newText = if (startKeyIndex != -1) text.substring(0, startKeyIndex) else text
                    val splitCollapseObj = SplitCollapse()
                    val content = splitCollapseObj.splitCollapse(newText)
                    content?.let {
                        imageList.addAll(splitCollapseObj.imageList)
                        _dataStack.addAll(it)
                    }
                }
                when {
                    startKeyIndex != -1 && startKeyIndex < endKeyIndex -> {
                        // 如果起始关键词位置 < 结束关键词位置
                        // 向关键词栈添加起始关键词位置
                        stack.add(startKeyIndex)
                        currentStart = startKeyIndex + 1
                        continue
                    }
                    startKeyIndex > endKeyIndex || (startKeyIndex == -1 && endKeyIndex != -1 && stack.isNotEmpty()) -> {
                        // 如果起始关键词位置 > 结束关键词位置，或者没有搜索到起始关键词，但是搜索到结束关键词且关键词栈不为空
                        // 从关键词栈中弹出起始关键词位置
                        val startIndex = stack.removeAt(stack.size - 1)
                        if (stack.isEmpty()) {
                            // 如果 statck 为空，则当前结果为一个区间
                            val quoteText = text.substring(startIndex + startKeyLength, endKeyIndex)
                            val quoteContent = SplitQuote().splitQuote(quoteText)
                            quoteContent?.let {
                                _dataStack.add(NgaContent.Quote(it, "quote"))
                            }
                        }
                        currentStart = endKeyIndex + 1
                        continue
                    }
                    startKeyIndex == -1 && endKeyIndex == -1 -> {
                        if (currentStart == 0) {
                            // 如果未搜索到 起始关键词和结束关键词，且起始位置为 0(未进行搜索)
                            // 直接返回文本，并进行下一步切割
                            val splitCollapseObj = SplitCollapse()
                            val content = splitCollapseObj.splitCollapse(text)
                            content?.let {
                                imageList.addAll(splitCollapseObj.imageList)
                            }
                            return content
                        } else {
                            // 如果起始位置不为 0
                            // aaa[quote]bbb[/quote]ccc
                            // 将之后的结果添加到数据堆栈 -> ccc
                            val newText = text.substring(currentStart + endKeyLength - 1)
                            val splitCollapseObj = SplitCollapse()
                            val content = splitCollapseObj.splitCollapse(newText)
                            content?.let {
                                imageList.addAll(splitCollapseObj.imageList)
                                _dataStack.addAll(it)
                            }
                        }
                        return _dataStack.takeIf { it.isNotEmpty() }
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e(TAG, "splitQuote: 解析错误", e)
            return listOf(NgaContent.Text(content = "数据异常"))
        }
    }
}