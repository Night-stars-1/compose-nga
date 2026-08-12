package com.srap.nga.logic.model

import com.google.gson.GsonBuilder
import com.srap.nga.logic.state.Code
import com.srap.nga.logic.state.CodeAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostCommentResponseTest {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Code::class.java, CodeAdapter())
        .create()

    @Test
    fun commentResponseReadsStringResult() {
        val response = gson.fromJson(
            """
            {
              "code": 0,
              "msg": "操作成功",
              "result": "发帖完毕 ..."
            }
            """.trimIndent(),
            PostCommentResponse::class.java,
        )

        assertEquals("发帖完毕 ...", response.resultMessage)
        assertEquals(Code.SUCCESS, response.code)
        assertEquals("操作成功", response.msg)
    }

    @Test
    fun commentResponseAcceptsObjectResult() {
        val response = gson.fromJson(
            """
            {
              "code": 0,
              "msg": "操作成功",
              "result": {"pid": 878149186}
            }
            """.trimIndent(),
            PostCommentResponse::class.java,
        )

        assertNull(response.resultMessage)
        assertEquals(
            878149186,
            response.result?.asJsonObject?.get("pid")?.asInt,
        )
    }

    @Test
    fun commentResponseIgnoresNonStringResults() {
        listOf("null", "[]", "123", "true").forEach { resultJson ->
            val response = gson.fromJson(
                "{\"code\":0,\"msg\":\"操作成功\",\"result\":$resultJson}",
                PostCommentResponse::class.java,
            )
            assertNull(response.resultMessage)
        }
    }
}
