package com.example.androidlab02

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

/**
 * 使用系统自带的 HttpURLConnection 发起 GET 请求，不依赖任何第三方网络库。
 * 请求参数放在线程中执行，调用方负责切回主线程更新界面。
 */
object HttpDemo {

    private const val TAG = "Lab02Net"
    private const val CONNECT_TIMEOUT_MS = 8000
    private const val READ_TIMEOUT_MS = 8000
    private const val BODY_LIMIT = 800

    fun fetch(address: String): String {
        var connection: HttpURLConnection? = null
        return try {
            val c = URL(address).openConnection() as HttpURLConnection
            connection = c
            c.requestMethod = "GET"
            c.connectTimeout = CONNECT_TIMEOUT_MS
            c.readTimeout = READ_TIMEOUT_MS
            // 关闭自动重定向，避免把 301/302 重定向误当成请求成功
            c.instanceFollowRedirects = false
            val status = c.responseCode
            val stream = if (status < 400) c.inputStream else c.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: "无响应正文"
            Log.i(TAG, "GET $address -> HTTP $status")
            "$address\nHTTP $status\n${body.take(BODY_LIMIT)}"
        } catch (e: Exception) {
            // 记录完整异常，便于在 logcat 中核对明文策略拦截原因
            Log.e(TAG, "GET $address -> FAILED ${e.javaClass.name}: ${e.message}", e)
            "$address\n请求失败：${e.javaClass.simpleName}\n${e.message}"
        } finally {
            connection?.disconnect()
        }
    }
}

