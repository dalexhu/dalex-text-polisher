package dev.dalex.textpolisher.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal fun buildHttpClient(timeoutMs: Long): OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .build()

internal fun Gson.extractErrorMessage(responseBody: String): String? =
    try {
        fromJson(responseBody, JsonObject::class.java)
            .getAsJsonObject("error")?.get("message")?.asString
    } catch (_: Exception) { null }
