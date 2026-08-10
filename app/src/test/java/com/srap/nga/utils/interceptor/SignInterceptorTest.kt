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
}
