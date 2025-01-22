package com.srap.nga.utils.nga.parse

import java.util.regex.Pattern

class SplitCollapse {
    private val stack = mutableListOf<Int>()
    private val dataStack = mutableListOf<NgaContent>()
    private val startKey = """\[collapse=.+?\]"""
    private val startKeyLength = "[collapse=".length
    private val endKey = "[/collapse]"
    private val endKeyLength = endKey.length

    fun splitCollapse(text: String, start: Int = 0): List<NgaContent>? {
        var currentStart = start

        while (true) {
            val startKeyPattern = Pattern.compile(this.startKey)
            val matcher = startKeyPattern.matcher(text.substring(currentStart))

            val startKeyIndex = if (matcher.find()) matcher.start() + currentStart else -1
            val endKeyIndex = text.indexOf(endKey, currentStart)
            if (currentStart == 0) {
                // 如果起始搜索位置为 0，则向数据堆栈添加关键词之前的内容
                // 该内容对进行下一步切割
                val newText = if (startKeyIndex != -1) text.substring(0, startKeyIndex) else text
                val content = SplitImage().splitImage(newText)
                content?.let { dataStack.addAll(it) }
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
                        val name = text.substring(startIndex + startKeyLength, text.indexOf("]", startIndex))
                        val newText = text.substring(startIndex + startKeyLength + name.length + 1, endKeyIndex)
                        val collapseContent = SplitCollapse().splitCollapse(newText)
                        collapseContent?.let {
                            dataStack.add(NgaContent.Collapse(name, it, "collapse"))
                        }
                    }
                    currentStart = endKeyIndex + 1
                    continue
                }
                startKeyIndex == -1 && endKeyIndex == -1 -> {
                    if (currentStart == 0) {
                        // 如果未搜索到 起始关键词和结束关键词，且起始位置为 0(未进行搜索)
                        // 直接返回文本
                        return SplitImage().splitImage(text)
                    } else {
                        // 如果起始位置不为 0
                        // aaa[collapse]bbb[/collapse]ccc
                        // 将之后的结果添加到数据堆栈 -> ccc
                        val newText = text.substring(currentStart + endKeyLength - 1)
                        val content = SplitImage().splitImage(newText)
                        content?.let { dataStack.addAll(content) }
                        return dataStack.takeIf { it.isNotEmpty() }
                    }
                }
            }
        }
    }
}
