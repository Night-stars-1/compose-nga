package com.srap.nga.utils.interceptor

import android.util.Log
import com.srap.nga.utils.StorageUtil
import com.srap.nga.utils.StringUtil
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response

class SignInterceptor : Interceptor {
    private val TAG = javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.i(TAG, "intercept: $request")
        // 构建新的请求
        val signedRequest = request.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Build/UKQ1.230917.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/123.0.6312.118 Mobile Safari/537.36")
            .header("X-USER-AGENT", "Nga_Official/90941(Xiaomi;Android 14)")
        val originalBody = request.body
        if (originalBody != null) {

            val appId = "1010"
            val accessUid = StorageUtil.Uid.toString()
            val accessToken = StorageUtil.Token
            val salt = "392e916a6d1d8b7523e2701470000c30bc2165a1"
            val timestamp = System.currentTimeMillis().toString()

            // 解析请求体
            val formData = parseFormBody(originalBody)

            val fid = formData.getOrDefault("fid", "")
            val tid = formData.getOrDefault("tid", "")
            val uid = formData.getOrDefault("uid", "")
            val key = formData.getOrDefault("key", "")

//            val sortedKeys = formData.keys.filter { !it.startsWith("__") }.sorted()
//            val combinedValues = sortedKeys.joinToString("") { formData[it] ?: "" }
            val md5String = "$appId$accessUid$accessToken$fid$tid$uid$key${timestamp}$salt"
            Log.d(TAG, "intercept: $md5String")
            val md5Signature = StringUtil.md5(md5String)

            val newBodyBuilder = FormBody.Builder()
                .add("access_uid", accessUid)
                .add("access_token", accessToken)
                .add("app_id", appId)
                .add("t", timestamp)
                .add("sign", md5Signature)

            formData.forEach {
                newBodyBuilder.add(it.key, it.value)
            }

            signedRequest.method(request.method, newBodyBuilder.build())
        }

        return chain.proceed(signedRequest.build())
    }
}


/**
 * 解析 FormBody 并转换为 Map<String, String>
 */
fun parseFormBody(requestBody: RequestBody?): MutableMap<String, String> {
    val formData = mutableMapOf<String, String>()

    if (requestBody is FormBody) {
        for (i in 0 until requestBody.size) {
            formData[requestBody.name(i)] = requestBody.value(i)
        }
    }
    return formData
}