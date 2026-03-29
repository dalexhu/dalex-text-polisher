package dev.dalex.textpolisher.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.dalex.textpolisher.prompt.PromptBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class OpenAiClient(
    private val apiKey: String,
    private val endpoint: String,
    private val model: String,
) : AiClient {

    private val gson = Gson()

    override fun complete(prompt: PromptBuilder.Prompt, timeoutMs: Long): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .build()

        val body = gson.toJson(mapOf(
            "model" to model,
            "messages" to listOf(
                mapOf("role" to "system", "content" to prompt.systemMessage),
                mapOf("role" to "user", "content" to prompt.userMessage),
            ),
        ))

        val request = Request.Builder()
            .url("${endpoint.trimEnd('/')}/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw RuntimeException("Empty response from OpenAI API")

        if (!response.isSuccessful) {
            val errorMsg = try {
                gson.fromJson(responseBody, JsonObject::class.java)
                    .getAsJsonObject("error")?.get("message")?.asString
            } catch (_: Exception) { null }
            throw RuntimeException("OpenAI API error (${response.code}): ${errorMsg ?: responseBody}")
        }

        val json = gson.fromJson(responseBody, JsonObject::class.java)
        return json.getAsJsonArray("choices")
            ?.firstOrNull()?.asJsonObject
            ?.getAsJsonObject("message")
            ?.get("content")?.asString
            ?: throw RuntimeException("Unexpected OpenAI response format")
    }
}
