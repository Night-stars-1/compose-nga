package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPollParserTest {
    private val pollHtml = """
        <div class="voteBlock" data-vote='{"max_select":5,"end":1783782407,"type":0,"min":0,"max":0}'>
          <form>
            <div class="voteTotalTitle"><h2>投票</h2></div>
            <div class="voteSubTitle">
              <label><input data-vid="205687" type="checkbox"/><h2>Re:从零开始的异世界生活 第4期</h2></label>
            </div>
            <div class="percentItem">
              <div class="percentTxt"><span class="percentNum">721</span>人</div>
              <div class="percentLine"><div class="percentScroll" style="width:16.555683122847%"></div></div>
            </div>
            <div class="voteSubTitle">
              <label><input data-vid="205688" type="checkbox"/><h2>这是一个很长、需要正常换行而不能挤坏右侧票数的选项</h2></label>
            </div>
            <div class="percentItem">
              <div class="percentTxt"><span class="percentNum">1,024</span>人</div>
              <div class="percentLine"><div class="percentScroll" style="width:23.51%"></div></div>
            </div>
            <div class="voteSubmit">投票</div>
            <div class="voteDigest"><p>最多选择5项 共计1737人投票 共计4355票<br>结束时间 2026-07-11 23:06</p></div>
          </form>
        </div>
    """.trimIndent()

    @Test
    fun `parses NGA poll metadata options and results`() {
        val poll = HtmlPollParser.parse(pollHtml)!!

        assertEquals("投票", poll.title)
        assertEquals(5, poll.maxSelect)
        assertEquals(1783782407L, poll.endTimestampSeconds)
        assertEquals(2, poll.items.size)
        assertEquals("205687", poll.items[0].id)
        assertEquals("Re:从零开始的异世界生活 第4期", poll.items[0].title)
        assertEquals(721, poll.items[0].voteCount)
        assertEquals(16.555683f, poll.items[0].percentage!!, 0.0001f)
        assertEquals(1024, poll.items[1].voteCount)
        assertEquals(23.51f, poll.items[1].percentage!!, 0.0001f)
        assertTrue(poll.digest.contains("结束时间 2026-07-11 23:06"))
    }

    @Test
    fun `split collapse keeps text around a poll in source order`() {
        val content = SplitCollapse().splitCollapse("前文${pollHtml}后文")!!

        assertEquals(3, content.size)
        assertEquals("前文", (content[0] as NgaContent.Text).content.trim())
        assertEquals(2, (content[1] as NgaContent.Poll).poll.items.size)
        assertEquals("后文", (content[2] as NgaContent.Text).content.trim())
    }
}
