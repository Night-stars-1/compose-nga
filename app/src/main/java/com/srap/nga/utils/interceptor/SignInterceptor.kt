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
            val timestamp = ngaTimestampSeconds(System.currentTimeMillis())

            // 解析请求体
            val formData = parseFormBody(originalBody)

            val fid = formData.getOrDefault("fid", EMPTY_STRING)
            val tid = formData.getOrDefault("tid", EMPTY_STRING)
            val pid = if (tid.isNotEmpty() && tid != "0") formData.getOrDefault("pid", EMPTY_STRING) else EMPTY_STRING
            val uid = formData["uid"]
                ?: formData["id"]
                ?: EMPTY_STRING
            val key = formData.getOrDefault("key", EMPTY_STRING)
            val value = formData.getOrDefault("value", EMPTY_STRING)
            val requestLib = request.url.queryParameter("__lib")
            val requestAction = request.url.queryParameter("__act")

//            val sortedKeys = formData.keys.filter { !it.startsWith("__") }.sorted()
//            val combinedValues = sortedKeys.joinToString("") { formData[it] ?: "" }
            val md5String = signPayload(
                lib = requestLib,
                action = requestAction,
                appId = appId,
                accessUid = accessUid,
                accessToken = accessToken,
                fid = fid,
                tid = tid,
                pid = pid,
                uid = uid,
                key = key,
                value = value,
                content = formData.getOrDefault("content", EMPTY_STRING),
                subject = formData.getOrDefault("subject", EMPTY_STRING),
                timestamp = timestamp,
            )
            val md5Signature = StringUtils.md5(md5String)

            val newBodyBuilder = FormBody.Builder()
                .add("access_uid", accessUid)
                .add("access_token", accessToken)
                .add("app_id", appId)
                .add("t", timestamp)
                .add("sign", md5Signature)
                .add("__output", "14")

            // 发帖、主题回复和评论回复都需要官方客户端校验值。
            if (requiresNgaClientChecksum(lib = requestLib, action = requestAction)) {
                newBodyBuilder.add(
                    "__ngaClientChecksum",
                    ngaClientChecksum(accessUid, timestamp),
                )
            }

            formData.forEach {
                newBodyBuilder.add(it.key, it.value)
            }

            signedRequest.method(request.method, newBodyBuilder.build())
        }

        return chain.proceed(signedRequest.build())
    }
}

internal fun ngaTimestampSeconds(currentTimeMillis: Long): String =
    (currentTimeMillis / 1000L).toString()

internal fun requiresNgaClientChecksum(lib: String?, action: String?): Boolean =
    lib == "post" && action in setOf("new", "reply", "quote")

/** 生成 NGA 普通 sign 的原始字符串；主题回复和评论回复把 tid、content 作为业务参数。 */
internal fun signPayload(
    lib: String?,
    action: String?,
    appId: String,
    accessUid: String,
    accessToken: String,
    fid: String,
    tid: String,
    pid: String,
    uid: String,
    key: String,
    value: String,
    content: String,
    subject: String,
    timestamp: String,
): String {
    val prefix = "$appId$accessUid$accessToken"
    return when {
        lib == "post" && action in setOf("reply", "quote") ->
            "$prefix$tid$content$timestamp${Constants.SALT}"
        lib == "post" && action == "new" ->
            "$prefix$fid$subject$content$timestamp${Constants.SALT}"
        else -> {
            "$prefix$fid$tid$pid$uid$key$value$timestamp${Constants.SALT}"
        }
    }
}

/** 根据官方 NGA 客户端算法生成发帖/回复校验值。 */
internal fun ngaClientChecksum(uid: String, timestamp: String): String {
    val md51 = StringUtils.md5("161c8c015f912abf4434c3263279c296$timestamp")
    val secret = StringUtils.md5("${md51}12d6af9da0f1fe4e3c87f76ce0aec149")
    return StringUtils.md5("$uid$secret$timestamp") + timestamp
}
