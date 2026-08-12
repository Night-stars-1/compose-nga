package com.srap.nga.utils.interceptor

import org.junit.Assert.assertEquals
import org.junit.Test

class SignInterceptorTest {
    @Test
    fun timestamp_usesUnixSeconds() {
        assertEquals(
            "1786331063",
            ngaTimestampSeconds(currentTimeMillis = 1_786_331_063_999L),
        )
    }

    @Test
    fun clientChecksum_matchesOfficialReplyRequest() {
        assertEquals(
            "97497a1dcb2ce42a412566ee9ca7714c1786497347",
            ngaClientChecksum("65096494", "1786497347"),
        )
    }

    @Test
    fun clientChecksum_matchesOfficialThreadReplyRequest() {
        assertEquals(
            "f5de41f6016cdce05a828cf4374fc56d1786513067",
            ngaClientChecksum("65096494", "1786513067"),
        )
    }

    @Test
    fun checksum_isAddedToPostMutationRequests() {
        assertEquals(true, requiresNgaClientChecksum("post", "new"))
        assertEquals(true, requiresNgaClientChecksum("post", "reply"))
        assertEquals(true, requiresNgaClientChecksum("post", "quote"))
        assertEquals(false, requiresNgaClientChecksum("post", "check"))
        assertEquals(false, requiresNgaClientChecksum("subject", "reply"))
    }

    @Test
    fun replySign_usesTidAndContent() {
        val payload = signPayload(
            lib = "post",
            action = "reply",
            appId = "1010",
            accessUid = "123456",
            accessToken = "test-token",
            fid = "823",
            tid = "39972744",
            pid = "",
            uid = "",
            key = "",
            value = "",
            content = "[s:ac:茶]",
            subject = "",
            timestamp = "1786513067",
        )
        assertEquals(
            "1010123456test-token39972744[s:ac:茶]1786513067" +
                "392e916a6d1d8b7523e2701470000c30bc2165a1",
            payload,
        )
    }

    @Test
    fun quoteSign_usesTidAndContent() {
        val payload = signPayload(
            lib = "post",
            action = "quote",
            appId = "1010",
            accessUid = "123456",
            accessToken = "test-token",
            fid = "510480",
            tid = "42845977",
            pid = "802076529",
            uid = "",
            key = "",
            value = "",
            content = "引用内容\n\n回复内容",
            subject = "",
            timestamp = "1786497347",
        )
        assertEquals(
            "1010123456test-token42845977引用内容\n\n回复内容1786497347" +
                "392e916a6d1d8b7523e2701470000c30bc2165a1",
            payload,
        )
    }

    @Test
    fun otherSign_keepsExistingFieldOrder() {
        val payload = signPayload(
            lib = "subject",
            action = "list",
            appId = "1010",
            accessUid = "1",
            accessToken = "token",
            fid = "7",
            tid = "42",
            pid = "9",
            uid = "3",
            key = "k",
            value = "v",
            content = "body",
            subject = "",
            timestamp = "1700000000",
        )
        assertEquals(
            "10101token74293kv1700000000392e916a6d1d8b7523e2701470000c30bc2165a1",
            payload,
        )
    }

    @Test
    fun newPostSign_usesForumSubjectAndContent() {
        val payload = signPayload(
            lib = "post",
            action = "new",
            appId = "1010",
            accessUid = "123456",
            accessToken = "test-token",
            fid = "7",
            tid = "",
            pid = "",
            uid = "",
            key = "",
            value = "",
            content = "[b][/b]\n[del][/del]",
            subject = "e",
            timestamp = "1786292464",
        )
        assertEquals(
            "1010123456test-token7e[b][/b]\n[del][/del]1786292464" +
                "392e916a6d1d8b7523e2701470000c30bc2165a1",
            payload,
        )
    }
}
