package com.srap.nga.utils.interceptor

import android.os.Handler
import android.os.Looper
import com.srap.nga.ui.navigateToLogin
import com.srap.nga.utils.GlobalObject
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val peekBody = response.peekBody(Long.MAX_VALUE)
        val responseBody = peekBody.string()
        try {
            val jsonObject = JSONObject(responseBody)
            val code = jsonObject.optInt("code")
            if (code == 46) {
                Handler(Looper.getMainLooper()).post {
                    GlobalObject.navController?.navigateToLogin()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }
}