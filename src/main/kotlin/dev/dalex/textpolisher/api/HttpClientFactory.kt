package dev.dalex.textpolisher.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// Shared client: reuses connection pool and thread pool across all requests.
// Per-call timeouts are applied via newBuilder() without allocating new pools.
internal val sharedHttpClient: OkHttpClient = OkHttpClient()

internal fun sharedHttpClient(timeoutMs: Long): OkHttpClient =
    sharedHttpClient.newBuilder()
        .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .build()

internal fun Gson.extractErrorMessage(responseBody: String): String? =
    try {
        fromJson(responseBody, JsonObject::class.java)
            .getAsJsonObject("error")?.get("message")?.asString
    } catch (_: Exception) { null }
