package com.srap.nga.utils.nga.parse

class SplitImage {
    private val stack = mutableListOf<Int>()
    private val dataStack = mutableListOf<NgaContent>()
    private val startKey = "[img]"
    private val startKeyLength = startKey.length
    private val endKey = "[/img]"
    private val endKeyLength = endKey.length

    fun splitImage(text: String, start: Int = 0): List<NgaContent>? {
        var currentStart = start

        while (true) {
            val startKeyIndex = text.indexOf(startKey, currentStart)
            val endKeyIndex = text.indexOf(endKey, currentStart)
            if (currentStart == 0) {
                // 如果起始搜索位置为 0，则向数据堆栈添加关键词之前的内容
                // 该内容对进行下一步切割
                val newText = if (startKeyIndex != -1) text.substring(0, startKeyIndex) else text
                if (newText.isNotEmpty()) {
                    val content = NgaContent.Text(content = newText)
                    dataStack.add(content)
                }
            } else if (currentStart < startKeyIndex && stack.isEmpty()) {
                // 关键词堆栈为空，说明上一个[img][/img]已经匹配完成
                // 由于图片的链接就是内容，只有两边的是文本，所以需要单独适配
                // 如果起始位置 < 起始关键词位置
                // 则中间还有文本
                val newText = text.substring(currentStart + this.endKeyLength - 1, startKeyIndex)
                val content = NgaContent.Text(content = newText)
                dataStack.add(content)
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
                        val url = text.substring(startIndex + startKeyLength, endKeyIndex)
                        dataStack.add(NgaContent.Image(url))
                    }
                    currentStart = endKeyIndex + 1
                    continue
                }
                startKeyIndex == -1 && endKeyIndex == -1 -> {
                    if (currentStart == 0) {
                        // 如果未搜索到 起始关键词和结束关键词，且起始位置为 0(未进行搜索)
                        // 直接返回文本
                        return if (text.isNotEmpty()) listOf(NgaContent.Text(text, "text")) else null
                    } else {
                        // 如果起始位置不为 0
                        // aaa[img]bbb[/img]ccc
                        // 将之后的结果添加到数据堆栈 -> ccc
                        val newText = text.substring(currentStart + endKeyLength - 1)
                        dataStack.add(NgaContent.Text(newText, "text"))
                        return dataStack.takeIf { it.isNotEmpty() }
                    }
                }
            }
        }
    }
}
