package com.srap.nga.utils.interceptor

import com.srap.nga.logic.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor(
    private val sessionManager: SessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val peekBody = response.peekBody(Long.MAX_VALUE)
        val responseBody = peekBody.string()
        try {
            val jsonObject = JSONObject(responseBody)
            val code = jsonObject.optInt("code")
            if (code == 46 || code == 2048) {
                // 更改登录状态为未登录
                sessionManager.markLoggedOut()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }
}
