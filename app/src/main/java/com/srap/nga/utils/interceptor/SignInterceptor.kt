package com.srap.nga.utils.interceptor

import android.util.Log
import com.srap.nga.constant.Constants
import com.srap.nga.constant.Constants.EMPTY_STRING
import com.srap.nga.utils.StorageUtils
import com.srap.nga.utils.StringUtils
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response

private const val TAG = "SignInterceptor"

class SignInterceptor : Interceptor {
    /**
     * 解析 FormBody 并转换为 Map<String, String>
     */
    private fun parseFormBody(requestBody: RequestBody?): MutableMap<String, String> {
        val formData = mutableMapOf<String, String>()

        if (requestBody is FormBody) {
            for (i in 0 until requestBody.size) {
                formData[requestBody.name(i)] = requestBody.value(i)
            }
        }
        return formData
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d(TAG, "intercept: $request")
        // 构建新的请求
        val signedRequest = request.newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Build/UKQ1.230917.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/123.0.6312.118 Mobile Safari/537.36")
            .header("X-USER-AGENT", "Nga_Official/90941(Xiaomi;Android 14)")
        val originalBody = request.body
        if (originalBody != null) {

            val appId = "1010"
            val accessUid = StorageUtils.Uid.toString()
            val accessToken = StorageUtils.Token
            val timestamp = System.currentTimeMillis().toString()

            // 解析请求体
            val formData = parseFormBody(originalBody)

            val fid = formData.getOrDefault("fid", EMPTY_STRING)
            val tid = formData.getOrDefault("tid", EMPTY_STRING)
            val uid = formData.getOrDefault("uid", EMPTY_STRING)
            val key = formData.getOrDefault("key", EMPTY_STRING)

//            val sortedKeys = formData.keys.filter { !it.startsWith("__") }.sorted()
//            val combinedValues = sortedKeys.joinToString("") { formData[it] ?: "" }
            val md5String = "$appId$accessUid$accessToken$fid$tid$uid$key${timestamp}${Constants.SALT}"
            Log.d(TAG, "intercept: $md5String")
            val md5Signature = StringUtils.md5(md5String)

            val newBodyBuilder = FormBody.Builder()
                .add("access_uid", accessUid)
                .add("access_token", accessToken)
                .add("app_id", appId)
                .add("t", timestamp)
                .add("sign", md5Signature)
                .add("__output", "14")

            formData.forEach {
                newBodyBuilder.add(it.key, it.value)
            }

            signedRequest.method(request.method, newBodyBuilder.build())
        }

        return chain.proceed(signedRequest.build())
    }
}
